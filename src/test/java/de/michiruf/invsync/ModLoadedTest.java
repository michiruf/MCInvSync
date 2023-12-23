package de.michiruf.invsync;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModLoadedTest {

    //@Test
    public void modLoadedTest() {
        Assertions.assertEquals(1, FabricLoader.getInstance().getAllMods().size());
        FabricLoader.getInstance().getAllMods()
                .forEach(modContainer -> System.out.println(modContainer.getMetadata().getId()));
        Assertions.assertTrue(FabricLoader.getInstance().isModLoaded("mr-incsync"));
    }

    //@Test
    public void testMyModFunctionality() {
        AtomicBoolean executed = new AtomicBoolean(false);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            Assertions.assertTrue(true);
            executed.set(true);
        });
        Awaitility.await()
                .atMost(Duration.of(1, ChronoUnit.MINUTES))
                .until(executed::get);
    }
}
