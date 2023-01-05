package mrnavastar.invsync.event;

import mrnavastar.invsync.Config;
import mrnavastar.invsync.data.ORMLite;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.concurrent.TimeUnit;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class DelegatingEventsHandler {

    public static void registerMinecraftEvents(ORMLite database, Config config) {
        registerMinecraftJoinEvent(database, config);
        registerMinecraftDisconnectEvent(database, config);
    }

    private static void registerMinecraftJoinEvent(ORMLite database, Config config) {
        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) ->
                PlayerDataService.loadPlayer(handler.getPlayer(), database, config)));
    }

    private static void registerMinecraftDisconnectEvent(ORMLite database, Config config) {
        ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) ->
                PlayerDataService.savePlayer(handler.getPlayer(), database, config)));
    }
}
