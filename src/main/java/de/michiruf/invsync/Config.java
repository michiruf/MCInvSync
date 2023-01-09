package de.michiruf.invsync;

import mc.microconfig.Comment;
import mc.microconfig.ConfigData;

public class Config implements ConfigData {

    @Comment("Allowed values: \"SQLITE\" | \"MYSQL\" | \"POSTGRES\"")
    public String DATABASE_TYPE = "SQLITE";

    // Sqlite
    public String SQLITE_PATH = "/path/to/database/InvSync.db";

    // Mysql
    public String MYSQL_DATABASE = "InvSync";
    public String MYSQL_ADDRESS = "127.0.0.1";
    public String MYSQL_PORT = "3306";
    public String MYSQL_USERNAME = "username";
    public String MYSQL_PASSWORD = "password";

    // Postgres
    public String POSTGRES_DATABASE = "InvSync";
    public String POSTGRES_ADDRESS = "127.0.0.1";
    public String POSTGRES_PORT = "5432";
    public String POSTGRES_USERNAME = "username";
    public String POSTGRES_PASSWORD = "password";

    @Comment("Sync settings")
    public boolean SYNC_INVENTORY = true;
    public boolean SYNC_ENDER_CHEST = true;
    public boolean SYNC_HEALTH = true;
    public boolean SYNC_FOOD_LEVEL = true;
    public boolean SYNC_XP_LEVEL = true;
    public boolean SYNC_SCORE = true;
    public boolean SYNC_STATUS_EFFECTS = true;
    public boolean SYNC_ADVANCEMENTS = true;
    public boolean SYNCHRONIZATION_DELAY = true;
    public int SYNCHRONIZATION_DELAY_SECONDS = 1;

    @Comment("Initial synchronization settings")
    public boolean INITIAL_SYNC_OVERWRITE_ENABLED = false;
    public String INITIAL_SYNC_SERVER_NAME = "undefined";
}
