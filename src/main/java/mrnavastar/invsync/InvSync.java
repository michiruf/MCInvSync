package mrnavastar.invsync;

import mc.microconfig.MicroConfig;
import mrnavastar.invsync.data.ORMLite;
import mrnavastar.invsync.data.PersistenceUtil;
import mrnavastar.invsync.event.InvSyncEventHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.ServerAdvancementLoader;
import org.apache.logging.log4j.Level;

import java.text.MessageFormat;

public class InvSync implements ModInitializer {

    public static Config config = MicroConfig.getOrCreate(InvSync.class.getSimpleName(), new Config());
    public static ORMLite database;
    public static ServerAdvancementLoader advancementLoader;

    @Override
    public void onInitialize() {
        Logger.log(Level.INFO, "Initializing InvSync...");
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
                case "H2" -> ORMLite.connectH2();
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
        InvSyncEventHandler.registerMinecraftEvents(database);
        InvSyncEventHandler.registerInvSyncEvents(config);
    }
}
