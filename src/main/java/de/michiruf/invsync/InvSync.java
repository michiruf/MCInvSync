package de.michiruf.invsync;

import de.michiruf.invsync.data.ORMLite;
import de.michiruf.invsync.data.PersistenceUtil;
import de.michiruf.invsync.event.DelegatingEventsHandler;
import de.michiruf.invsync.event.InvSyncEventsHandler;
import mc.microconfig.MicroConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.ServerAdvancementLoader;
import org.apache.logging.log4j.Level;

import java.text.MessageFormat;

public class InvSync implements ModInitializer {

    public static InvSync instance;

    public Config config = MicroConfig.getOrCreate(InvSync.class.getSimpleName(), new Config());
    public ORMLite database;
    public ServerAdvancementLoader advancementLoader;

    @Override
    public void onInitialize() {
        Logger.log(Level.INFO, "Initializing InvSync...");
        instance = this;
        initDatabase();
        registerEvents();
        Logger.log(Level.INFO, "Initialized InvSync");
    }

    private void initDatabase() {
        try {
            PersistenceUtil.registerCustomPersisters();
            database = switch (config.DATABASE_TYPE) {
                case "SQLITE" -> ORMLite.connectSQLITE(config.SQLITE_PATH);
                case "MYSQL" -> ORMLite.connectMySQL(
                        config.MYSQL_DATABASE,
                        config.MYSQL_ADDRESS,
                        config.MYSQL_PORT,
                        config.MYSQL_USERNAME,
                        config.MYSQL_PASSWORD);
//                case "POSTGRES" -> throw new IllegalArgumentException("POSTGRES not yet supported");
                case "POSTGRES" -> ORMLite.connectPostgres(
                        config.POSTGRES_DATABASE,
                        config.POSTGRES_ADDRESS,
                        config.POSTGRES_PORT,
                        config.POSTGRES_USERNAME,
                        config.POSTGRES_PASSWORD);
                default -> null;
            };
        } catch (Exception e) {
            Logger.log(Level.ERROR, "Database could not get initialized");
            Logger.logException(Level.ERROR, e);
            System.exit(1);
            return;
        }

        if (database == null) {
            Logger.log(Level.ERROR, MessageFormat.format("Configured database type {0} is not available", config.DATABASE_TYPE));
            System.exit(1);
        }
    }

    private void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> advancementLoader = server.getAdvancementLoader());
        DelegatingEventsHandler.registerMinecraftEvents(database, config);
        InvSyncEventsHandler.registerEvents(config);
    }

    // TODO Register a command to synchronize offline players
//    private static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
//        LiteralCommandNode<ServerCommandSource> proxyCommand = CommandManager
//                .literal("invsync")
//                .requires(cmd -> cmd.hasPermissionLevel(4))
//                .then(CommandManager.argument("command", StringArgumentType.string())
//                        .executes(ProxyCommandMod::sendMessage)
//                        .build())
//                .build();
//        dispatcher.getRoot().addChild(proxyCommand);
//    }
}
