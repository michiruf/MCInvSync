//package de.michiruf.invsync.good_tries;
//
//import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
//import com.mojang.datafixers.DataFixer;
//import com.mojang.datafixers.DataFixerBuilder;
//import net.minecraft.resource.ResourcePackManager;
//import net.minecraft.resource.ResourceType;
//import net.minecraft.server.SaveLoader;
//import net.minecraft.server.WorldGenerationProgressListenerFactory;
//import net.minecraft.server.WorldGenerationProgressLogger;
//import net.minecraft.server.dedicated.MinecraftDedicatedServer;
//import net.minecraft.server.dedicated.ServerPropertiesLoader;
//import net.minecraft.util.ApiServices;
//import net.minecraft.world.level.storage.LevelStorage;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.io.File;
//import java.net.Proxy;
//import java.nio.file.Path;
//import java.util.UUID;
//
//public class DedicatedTestServer extends MinecraftDedicatedServer {
//
//    private static final Logger logger = LogManager.getLogger();
//
//    private DedicatedTestServer(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, ServerPropertiesLoader propertiesLoader, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
//        super(serverThread, session, dataPackManager, saveLoader, propertiesLoader, dataFixer, apiServices, worldGenerationProgressListenerFactory);
//    }
//
//    public static DedicatedTestServer startServer(File file, Thread serverThread) throws Throwable {
//        var service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
//        var sessionService = service.createMinecraftSessionService();
//        var profileRepository = service.createProfileRepository();
//
//
//        var levelStorage = LevelStorage.create(Path.of("LEVELSTORAGE"));
//        var session = levelStorage.createSession("SESSION");
//        var resourcePackManager = new ResourcePackManager(ResourceType.SERVER_DATA);
//        var saveLoader = new SaveLoader();
//        var serverPropertiesLoader = new ServerPropertiesLoader();
//        var dataFixer = new DataFixerBuilder(1)
//                .buildUnoptimized();
//        var apiService = new ApiServices();
//
//        var dedicatedTestServer = new DedicatedTestServer(
//                serverThread,
//                session,
//                resourcePackManager,
//                saveLoader,
//                serverPropertiesLoader,
//                dataFixer,
//                apiService,
//                WorldGenerationProgressLogger::new);
//        return dedicatedTestServer;
//    }
//}
