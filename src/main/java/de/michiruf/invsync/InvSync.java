package de.michiruf.invsync;

import de.michiruf.invsync.config.Config;
import de.michiruf.invsync.config.ConfigWrapper;
import de.michiruf.invsync.data.ORMLite;
import de.michiruf.invsync.data.PersistenceUtil;
import de.michiruf.invsync.event.DelegatingEventsHandler;
import de.michiruf.invsync.event.InvSyncEventsHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.ServerAdvancementLoader;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.text.MessageFormat;

public class InvSync implements ModInitializer {

    public static InvSync instance;
    public Config config;

    public ORMLite database;
    public ServerAdvancementLoader advancementLoader;

    @Override
    public void onInitialize() {
        Logger.log(Level.INFO, "Initializing InvSync...");
        instance = this;
        initConfig();
        initDatabase();
        registerEvents();
        Logger.log(Level.INFO, "Initialized InvSync");
    }

    private void initConfig() {
        try {
            config = ConfigWrapper.load().config;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initDatabase() {
        try {
            PersistenceUtil.registerCustomPersisters();
            database = switch (config.databaseType) {
                case SQLITE -> ORMLite.connectSQLITE(config.sqlite.path, config.debugDeleteTables);
                case MYSQL -> ORMLite.connect(
                        "mysql",
                        config.mysql.database,
                        config.mysql.address,
                        config.mysql.port,
                        config.mysql.username,
                        config.mysql.password,
                        config.debugDeleteTables);
                case POSTGRES -> ORMLite.connect(
                        "postgres",
                        config.postgres.database,
                        config.postgres.address,
                        config.postgres.port,
                        config.postgres.username,
                        config.postgres.password,
                        config.debugDeleteTables);
                default -> null;
            };
        } catch (Exception e) {
            Logger.log(Level.ERROR, "Database could not get initialized");
            Logger.logException(Level.ERROR, e);
            System.exit(1);
            return;
        }

        if (database == null) {
            Logger.log(Level.ERROR, MessageFormat.format("Configured database type {0} is not available", config.databaseType));
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
