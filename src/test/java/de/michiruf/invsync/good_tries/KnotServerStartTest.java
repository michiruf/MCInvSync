package de.michiruf.invsync.good_tries;

import de.michiruf.invsync.base.TestGameProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.fabricmc.loader.impl.launch.knot.Knot;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.NotImplementedException;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class KnotServerStartTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnotServerStartTest.class);
    public boolean serverInitialized;
    public MinecraftServer server;

    private void startupUsingDevlaunchinjector() throws Throwable {
        var args = "-Dfabric.dli.config=C:\\dev\\projects\\InvSync\\.gradle\\loom-cache\\launch.cfg -Dfabric.dli.env=server -Dfabric.dli.main=net.fabricmc.loader.impl.launch.knot.KnotServer";
        FabricLoader.getInstance().getGameDir();
        System.setProperty("fabric.dli.env", "server");
        System.setProperty("fabric.dli.main", "net.fabricmc.loader.impl.launch.knot.KnotServer");
        System.setProperty("fabric.dli.config", ".gradle/loom-cache/launch.cfg");
        // Next line works, but events are not processed and we cannot instantiate 2 servers yet
        net.fabricmc.devlaunchinjector.Main.main(new String[]{"nogui"});
    }

    private void startupUsingKnot() throws Throwable {
        // TODO Try fabric.loader.useCompatibilityClassLoader ?
        System.setProperty("fabric.development", "true");
        System.setProperty("log4j.configurationFile", "C:\\dev\\projects\\InvSync\\.gradle\\loom-cache\\log4j.xml");
        System.setProperty("log4j2.formatMsgNoLookups", "true");
        System.setProperty("fabric.log.disableAnsi", "false");
        System.setProperty("fabric.remapClasspathFile", "C:\\dev\\projects\\InvSync\\.gradle\\loom-cache\\remapClasspath.txt");
        TestGameProvider.register();
        //Knot.launch(new String[]{"nogui"}, EnvType.SERVER);
        //Knot.launch(new String[]{"--help"}, EnvType.SERVER);

        System.setProperty("user.dir", "C:\\TEST");
        Knot.launch(new String[]{
                "nogui",
                "--port", "12345",
                "--serverId", "TEST",
                //"--gameDir", "C:\\TEST", // No effect on servers
                "--universe", "TEST1"
        }, EnvType.SERVER);
    }

    private void startupUsingReflection() throws Throwable {
        // Knot.setupUncaughtExceptionHandler();
        var m1 = FabricLauncherBase.class.getDeclaredMethod("setupUncaughtExceptionHandler");
        m1.setAccessible(true);
        m1.invoke(null);
        var knot = new Knot(EnvType.SERVER);
        // ClassLoader cl = knot.init(new String[]{"nogui"});
        var m2 = Knot.class.getDeclaredMethod("init", String[].class);
        m2.setAccessible(true);
        var cl = (ClassLoader) m2.invoke(knot, (Object) new String[]{"nogui"});
        // knot.provider.launch(cl);
        var m3 = Knot.class.getDeclaredField("provider");
        m3.setAccessible(true);
        var provider = (GameProvider) m3.get(knot);

        registerEvents();

        provider.launch(cl);
    }

    private void startupUsingHeadless() {
//        FabricGameTestHelper.runHeadlessServer(null, null);
//        TestServer.startServer(thread -> {
//           TestServer.create(thread, new LevelStorage.Session("world"));
//        });
        throw new NotImplementedException();
    }

    private void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.error("ServerLifecycleEvents.SERVER_STARTED");
            this.server = server;
            serverInitialized = true;
        });

        ServerWorldEvents.LOAD.register((server1, world) -> {
            LOGGER.error("ServerWorldEvents.LOAD");
        });
    }

    @Test
    public void testServerStartup() throws Throwable {
        registerEvents();

        LOGGER.error("BEFORE START");

//        startupUsingDevlaunchinjector();
        startupUsingKnot();
//        startupUsingReflection();
//        startupUsingHeadless();

        LOGGER.error("AFTER START");

        registerEvents();

        Awaitility.await()
                .atMost(Duration.of(1, ChronoUnit.MINUTES))
                .until(() -> serverInitialized);
        Assertions.assertNotNull(server, "Server didn't start successfully.");
    }

    @AfterEach
    public void shutdownServer() {
        if (server != null) {
            server.close();
        }
    }
}
