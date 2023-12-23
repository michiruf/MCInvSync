package de.michiruf.invsync;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class FabricMCExampleTestNotWorkingTest implements ModInitializer {

    public boolean serverInitialized;
    public MinecraftServer server;

    @Override
    public void onInitialize() {
        System.err.println("TEST");
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            serverInitialized = true;
        });
    }

    //@Test
    public void testServerStartup() {
        Awaitility.await()
                .atMost(Duration.of(1, ChronoUnit.MINUTES))
                .until(() -> serverInitialized);
        Assertions.assertNotNull(server, "Server didn't start successfully.");
    }
}
