package mrnavastar.invsync.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

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
}
