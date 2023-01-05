package mrnavastar.invsync.event;

import mrnavastar.invsync.data.entity.PlayerData;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public class InvSyncEvents {

    public static final Event<PlayerDataHandler> FETCH_PLAYER_DATA = EventFactory.createArrayBacked(PlayerDataHandler.class, callbacks -> (player, playerData) -> {
        for (var callback : callbacks)
            if (playerData != null)
                callback.handle(player, playerData);
    });

    public static final Event<PlayerDataHandler> SAVE_PLAYER_DATA = EventFactory.createArrayBacked(PlayerDataHandler.class, callbacks -> (player, playerData) -> {
        for (var callback : callbacks)
            callback.handle(player, playerData);
    });

    @FunctionalInterface
    public interface PlayerDataHandler {

        void handle(ServerPlayerEntity player, PlayerData playerData);
    }
}
