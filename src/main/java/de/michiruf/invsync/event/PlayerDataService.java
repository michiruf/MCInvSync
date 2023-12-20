package de.michiruf.invsync.event;

import de.michiruf.invsync.config.Config;
import de.michiruf.invsync.Logger;
import de.michiruf.invsync.data.ORMLite;
import de.michiruf.invsync.data.entity.PlayerData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.Level;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class PlayerDataService {

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
                if (playerData == null)
                    playerData = new PlayerData(player.getUuid());

                InvSyncEvents.SAVE_PLAYER_DATA.invoker().handle(player, playerData);
                playerData.prepareSave(config);
                database.playerDataDao.createOrUpdate(playerData);

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
