package de.michiruf.invsync.event;

import de.michiruf.invsync.Logger;
import de.michiruf.invsync.data.InventorySaveManager;
import de.michiruf.invsync.data.entity.PlayerData;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;

public class InvSyncEvents {

    public static final Event<PlayerDataHandler> FETCH_PLAYER_DATA = EventFactory.createArrayBacked(PlayerDataHandler.class, callbacks -> (player, playerData) -> {
        for (var callback : callbacks)
            if (playerData != null)
                try {
                    callback.handle(player, playerData);
                    InventorySaveManager.removePlayerFlag(player);
                } catch (Exception e) {
                    Logger.logException(Level.ERROR, e);
                    InventorySaveManager.disableInventorySave(player);
                    player.networkHandler.disconnect(Text.of("Inventory could not be loaded correctly"));
                }
            else
                Logger.log(Level.WARN, "PlayerData is null");
    });

    public static final Event<PlayerDataHandler> SAVE_PLAYER_DATA = EventFactory.createArrayBacked(PlayerDataHandler.class, callbacks -> (player, playerData) -> {
        for (var callback : callbacks) {
            try {
                if (!InventorySaveManager.shouldSaveInventory(player)) {
                    // Skip saving inventory
                    return;
                }
                callback.handle(player, playerData);
            } catch (Exception e) {
                Logger.logException(Level.ERROR, e);
            }
        InventorySaveManager.removePlayerFlag(player);
            }
    });

    @FunctionalInterface
    public interface PlayerDataHandler {

        void handle(ServerPlayerEntity player, PlayerData playerData) throws Exception;
    }
}
