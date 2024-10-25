package de.michiruf.invsync.scheduler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TickScheduler {
    // Queue for one-off tasks
    private static final Queue<ScheduledTask> oneOffTasks = new LinkedList<>();
    // Map for repeating tasks, identified by UUID
    private static final ConcurrentHashMap<UUID, RepeatingTask> repeatingTasks = new ConcurrentHashMap<>();


    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(TickScheduler::onEndTick);
    }

    private static void onEndTick(MinecraftServer server) {
        // Handle one-off tasks
        Iterator<ScheduledTask> oneOffIterator = oneOffTasks.iterator();
        while (oneOffIterator.hasNext()) {
            ScheduledTask task = oneOffIterator.next();
            task.decrementTicks();
            if (task.isReady()) {
                try {
                    task.execute();
                } catch (Exception e) {
                    // Log the exception and continue with other tasks
                    TickSchedulerHelper.LOGGER.error("Exception while executing one-off task", e);
                }
                oneOffIterator.remove();
            }
        }

        // Handle repeating tasks
        Iterator<UUID> repeatingIterator = repeatingTasks.keySet().iterator();
        while (repeatingIterator.hasNext()) {
            UUID taskId = repeatingIterator.next();
            RepeatingTask task = repeatingTasks.get(taskId);
            if (task != null) {
                task.decrementTicks();
                if (task.isReady()) {
                    try {
                        task.execute();
                        task.resetTicks(); // Reset ticks for the next execution
                    } catch (Exception e) {
                        // Log the exception and remove the task to prevent endless failures
                        TickSchedulerHelper.LOGGER.error("Exception while executing repeating task", e);
                        repeatingIterator.remove();
                    }
                }
            }
        }
    }

    /**
     * Schedules a one-off task to be executed after a specified delay.
     *
     * @param runnable   The task to execute.
     * @param delayTicks The delay in ticks before execution.
     */
    public static void schedule(Runnable runnable, int delayTicks) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable task cannot be null");
        }
        if (delayTicks < 0) {
            throw new IllegalArgumentException("Delay ticks cannot be negative");
        }
        oneOffTasks.add(new ScheduledTask(runnable, delayTicks));
    }

    /**
     * Schedules a repeating task to be executed at fixed intervals.
     *
     * @param runnable          The task to execute.
     * @param intervalTicks     The interval in ticks between executions.
     * @return A UUID representing the scheduled repeating task.
     */
    public static UUID scheduleRepeating(Runnable runnable, int intervalTicks) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable task cannot be null");
        }
        if (intervalTicks <= 0) {
            throw new IllegalArgumentException("Interval ticks must be positive");
        }
        UUID taskId = UUID.randomUUID();
        repeatingTasks.put(taskId, new RepeatingTask(runnable, intervalTicks));
        return taskId;
    }

    /**
     * Cancels a repeating task based on its UUID.
     *
     * @param taskId The UUID of the repeating task to cancel.
     * @return True if the task was successfully canceled, false otherwise.
     */
    public static boolean cancelRepeating(UUID taskId) {
        if (taskId == null) {
            return false;
        }
        return repeatingTasks.remove(taskId) != null;
    }

    /**
     * Clears all scheduled tasks.
     * Use with caution; typically for debugging or mod shutdown scenarios.
     */
    public static void clearAllTasks() {
        oneOffTasks.clear();
        repeatingTasks.clear();
    }

    /**
     * Internal class representing a one-off scheduled task.
     */
    private static class ScheduledTask {
        private final Runnable runnable;
        private int ticksRemaining;

        public ScheduledTask(Runnable runnable, int ticks) {
            this.runnable = runnable;
            this.ticksRemaining = ticks;
        }

        public void decrementTicks() {
            if (ticksRemaining > 0) {
                ticksRemaining--;
            }
        }

        public boolean isReady() {
            return ticksRemaining <= 0;
        }

        public void execute() {
            runnable.run();
        }
    }

    /**
     * Internal class representing a repeating scheduled task.
     */
    private static class RepeatingTask {
        private final Runnable runnable;
        private final int intervalTicks;
        private int ticksRemaining;

        public RepeatingTask(Runnable runnable, int intervalTicks) {
            this.runnable = runnable;
            this.intervalTicks = intervalTicks;
            this.ticksRemaining = intervalTicks;
        }

        public void decrementTicks() {
            if (ticksRemaining > 0) {
                ticksRemaining--;
            }
        }

        public boolean isReady() {
            return ticksRemaining <= 0;
        }

        public void execute() {
            runnable.run();
        }

        public void resetTicks() {
            this.ticksRemaining = intervalTicks;
        }
    }
}