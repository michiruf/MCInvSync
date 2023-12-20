package de.michiruf.invsync.config;

import de.michiruf.invsync.config.database.DatabaseConnectionConfig;
import de.michiruf.invsync.config.database.SqliteConfig;
import de.michiruf.invsync.config.sync.InitialSyncConfig;
import de.michiruf.invsync.config.sync.SyncConfig;
import de.michiruf.invsync.config.sync.SyncOptionsConfig;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class Config {

    public DatabaseType databaseType = DatabaseType.SQLITE;
    public boolean debugDeleteTables = false;

    public SqliteConfig sqlite = new SqliteConfig("/path/to/database/InvSync.db");
    public DatabaseConnectionConfig mysql = new DatabaseConnectionConfig(
            "InvSync",
            "127.0.0.1",
            3306,
            "username",
            "password"
    );
    public DatabaseConnectionConfig postgres = new DatabaseConnectionConfig(
            "InvSync",
            "127.0.0.1",
            5432,
            "username",
            "password"
    );

    public SyncConfig sync = new SyncConfig();
    public SyncOptionsConfig syncOptions = new SyncOptionsConfig();
    public InitialSyncConfig initialSync = new InitialSyncConfig();

    public enum DatabaseType {

        SQLITE,
        MYSQL,
        POSTGRES
    }
}
