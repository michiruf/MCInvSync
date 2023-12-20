package de.michiruf.invsync.event;

import de.michiruf.invsync.config.Config;
import de.michiruf.invsync.data.ORMLite;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

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
