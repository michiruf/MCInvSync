package de.michiruf.invsync.event;

import de.michiruf.invsync.config.Config;
import de.michiruf.invsync.Logger;
import de.michiruf.invsync.data.ORMLite;
import de.michiruf.invsync.data.entity.PlayerData;
import de.michiruf.invsync.data.entity.PlayerDataHistory;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import de.michiruf.invsync.scheduler.TickScheduler;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class PlayerDataService {

    // fakeDimensionSwap is actually used to fix mods that need to reload when switching servers
    public static void fakeDimensionSwap(ServerPlayerEntity player, MinecraftServer server) {
        Logger.log(Level.INFO, "fake dimension swap start");
        ServerWorld currentWorld = (ServerWorld) player.getWorld();
        RegistryKey<World> fakeDimension = World.NETHER;
        if (currentWorld.getDimensionEntry() == World.NETHER.getRegistry()) {
            fakeDimension = World.END;
        }
        ServerWorld fakeWorld = server.getWorld(fakeDimension);
        Vec3d originalPos = player.getPos();
        TickScheduler.schedule(() -> {
            // Send the packet to the client
            FabricDimensions.teleport(player, fakeWorld, new TeleportTarget(Vec3d.ZERO.add(128, 128, 128), Vec3d.ZERO, player.getYaw(), player.getPitch()));
        }, 5);
        TickScheduler.schedule(() -> {
            FabricDimensions.teleport(player, currentWorld, new TeleportTarget(originalPos, Vec3d.ZERO, player.getYaw(), player.getPitch()));
            Logger.log(Level.INFO, "fake dimension swap end");
        }, 10);

    }

    public static void loadPlayer(ServerPlayerEntity player, ORMLite database, Config config) {
        Logger.log(Level.DEBUG, "Player JOIN event received");

        if (!config.syncOptions.applySynchronizationDelay) {
            loadPlayerImpl(player, database, config);
            return;
        }

        // The reason, why this delay might be needed, is that some proxy server might connect
        // the player to the next server before disconnecting the player from the previous
        // server. To avoid race conditions, we just go for a timeout on loading the new
        // inventory data
        // Unfortunately, fabric does not have a scheduler, so we just go with the good old
        // plain java thread and sleep, or we go with the java timer schedule method
        switch (config.syncOptions.synchronizationDelayType) {
            case SLEEP -> new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(config.syncOptions.synchronizationDelaySeconds);
                    loadPlayerImpl(player, database, config);
                } catch (InterruptedException e) {
                    Logger.logException(Level.ERROR, e);
                }
            }).start();
            case TIMER -> new Timer().schedule(
                    new RunnableTimerTask(() -> loadPlayerImpl(player, database, config)),
                    config.syncOptions.synchronizationDelaySeconds * 1000L);
            default -> throw new IllegalArgumentException(MessageFormat.format(
                    "Synchronization delay method is set to an unknown value \"{0}\"",
                    config.syncOptions.synchronizationDelayType));
        }
    }

    private static void loadPlayerImpl(ServerPlayerEntity player, ORMLite database, Config config) {
        database.transaction(() -> {
            try {
                var playerData = database.playerDataDao.queryForId(player.getUuidAsString());
                if (playerData == null) {
                    Logger.log(Level.INFO, "Player JOIN event not processed because player is new");
                    return;
                }

                // If initial sync enabled, sync mode OVERWRITE and the server name is not contained in the
                // initial servers list, we do not need to process the data here, because the data should get
                // lost by the overwrite mechanism anyway
                if (config.initialSync.initialSyncOverwriteEnabled &&
                        !Arrays.asList(playerData.initializedServers).contains(config.initialSync.initialSyncServerName)) {
                    Logger.log(Level.INFO, "Player JOIN event not processed because data shell be overwritten");
                    return;
                }

                InvSyncEvents.FETCH_PLAYER_DATA.invoker().handle(player, playerData);
                Logger.log(Level.DEBUG, "Player JOIN event processed");
            } catch (Exception e) {
                Logger.logException(Level.ERROR, e);
            }
        }, e -> Logger.logException(Level.ERROR, e));
    }

    public static void savePlayer(ServerPlayerEntity player, ORMLite database, Config config) {
        Logger.log(Level.DEBUG, "Player DISCONNECT event received");

        database.transaction(() -> {
            try {
                PlayerData playerData = database.playerDataDao.queryForId(player.getUuidAsString());
                if (playerData == null) {
                    playerData = new PlayerData(player.getUuid());
                }

                playerData.playerUsername = player.getDisplayName().getString();

                InvSyncEvents.SAVE_PLAYER_DATA.invoker().handle(player, playerData);
                playerData.prepareSave(config);
                database.playerDataDao.createOrUpdate(playerData);

                Logger.log(Level.DEBUG, "Player DISCONNECT event processed");
            } catch (Exception e) {
                Logger.logException(Level.ERROR, e);
            }
        }, e -> Logger.logException(Level.ERROR, e));

        // Save history
        database.transaction(() -> {
            try {
                Date insertDate = java.sql.Date.from(Instant.now());
                PlayerDataHistory history = new PlayerDataHistory(player.getUuid(), insertDate);

                PlayerData playerData = database.playerDataDao.queryForId(player.getUuidAsString());
                if (playerData != null) {
                    history.creationDate = playerData.date;
                    history.playerUsername = playerData.playerUsername;
                    history.advancements = playerData.advancements;
                    history.effects = playerData.effects;
                    history.health = playerData.health;
                    history.enderChest = playerData.enderChest;
                    history.inventory = playerData.inventory;
                    history.hunger = playerData.hunger;
                    history.initializedServers = playerData.initializedServers;
                    history.playerUuid = playerData.playerUuid;
                    history.xp = playerData.xp;
                    history.xpProgress = playerData.xpProgress;
                    history.trinkets = playerData.trinkets;
                    history.score = playerData.score;
                    history.selectedSlot = playerData.selectedSlot;
                }

                history.prepareSave(config);
                database.playerDataHistoryDao.create(history);

                Logger.log(Level.DEBUG, "Player DISCONNECT event processed");
            } catch (Exception e) {
                Logger.logException(Level.ERROR, e);
            }
        }, e -> Logger.logException(Level.ERROR, e));
    }

    private static class RunnableTimerTask extends TimerTask {

        private final Runnable runnable;

        public RunnableTimerTask(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } catch (Exception e) {
                Logger.logException(Level.ERROR, e);
            }
        }
    }
}
