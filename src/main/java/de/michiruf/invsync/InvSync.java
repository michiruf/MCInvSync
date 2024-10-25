package de.michiruf.invsync;

import de.michiruf.invsync.config.Config;
import de.michiruf.invsync.config.ConfigWrapper;
import de.michiruf.invsync.data.ORMLite;
import de.michiruf.invsync.data.PersistenceUtil;
import de.michiruf.invsync.data.entity.Statistics;
import de.michiruf.invsync.event.DelegatingEventsHandler;
import de.michiruf.invsync.event.InvSyncEventsHandler;
import de.michiruf.invsync.scheduler.TickScheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class InvSync implements ModInitializer {

    public static InvSync instance;
    public Config config;

    public ORMLite database;
    public ServerAdvancementLoader advancementLoader;
    private static MinecraftServer SERVER_INSTANCE;

    @Override
    public void onInitialize() {
        Logger.log(Level.INFO, "Initializing InvSync...");
        // Initialize the TickScheduler
        TickScheduler.initialize();
        instance = this;
        initConfig();
        initDatabase();
        registerEvents();
        registerStatistics();
        Logger.log(Level.INFO, "Initialized InvSync");
    }

    private void registerStatistics() {
        TickScheduler.scheduleRepeating(() -> {
            // Performance Metrics
            double tps = calculateTPS();
            long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
            long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
            int loadedChunks = getTotalLoadedChunks();
            long entityCount = getTotalEntityCount();
            long uptimeSeconds = getServer().getTicks() / 20;

            // Player Activity Metrics
            int onlinePlayers = getServer().getPlayerManager().getPlayerList().size();
            long totalPing = 0;
            double averagePing = 0.0;
            if (onlinePlayers > 0) {
                totalPing = getServer().getPlayerManager().getPlayerList().stream()
                        .mapToInt(player -> player.networkHandler.getPlayer().pingMilliseconds)
                        .sum();
                averagePing = (double) totalPing / onlinePlayers;
            }

            Date insertDate = java.sql.Date.from(Instant.now());
            Statistics stats = new Statistics(insertDate);

            stats.serverName = config.serverName;
            stats.serverTick = tps;
            stats.memoryUsage = usedMemory;
            stats.maxMemory = maxMemory;
            stats.loadedChunks = loadedChunks;
            stats.entityCount = entityCount;
            stats.uptimeSeconds = uptimeSeconds;
            stats.averagePlayerPing = averagePing;
            stats.playersOnline = onlinePlayers;
            stats.averagePlayerDeathCount = getTotalPlayerDeaths();
            stats.averagePlayerLevel = getAveragePlayerLevel();


            stats.prepareSave(config);
            try {
                database.statisticsDao.create(stats);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }, 1200);
    }

    private double calculateTPS() {
        getServer().getTickTime();
        return 20.0;
    }

    private long getTotalPlayerDeaths() {
        return getServer().getPlayerManager().getPlayerList().stream()
                .mapToInt(player -> player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS)))
                .sum();
    }

    private double getAveragePlayerLevel() {
        long totalPlayerLevels = 0;
        List<ServerPlayerEntity> players = getServer().getPlayerManager().getPlayerList();
        if (players.isEmpty()) {
            return 0;
        }
        for (ServerPlayerEntity p : players) {
            totalPlayerLevels += p.experienceLevel;
        }
        return (double) totalPlayerLevels / players.size();
    }

    private int getTotalLoadedChunks() {
        Iterable<ServerWorld> worldsIterable = getServer().getWorlds();
        Stream<ServerWorld> worldsStream = StreamSupport.stream(worldsIterable.spliterator(), false);
        return worldsStream.mapToInt(world -> world.getChunkManager().getLoadedChunkCount())
                .sum();
    }

    private long getTotalEntityCount() {
        Iterable<ServerWorld> worldsIterable = getServer().getWorlds();
        long totalEntities = 0;
        for (ServerWorld world : worldsIterable) {
            Iterable<Entity> entitiesIterable = world.iterateEntities();
            for (Entity entity : entitiesIterable) {
                totalEntities++;
            }
        }
        return totalEntities;
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
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStart);
        DelegatingEventsHandler.registerMinecraftEvents(database, config);
        InvSyncEventsHandler.registerEvents(config);
    }

    private void onServerStart(MinecraftServer server) {
        SERVER_INSTANCE = server;
        Logger.log(Level.DEBUG, "Server started. Server instance captured.");
    }

    public static MinecraftServer getServer() {
        return SERVER_INSTANCE;
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
