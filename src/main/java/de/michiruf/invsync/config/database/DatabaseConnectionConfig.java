package de.michiruf.invsync.config.database;

/**
 * @author Michael Ruf
 * @since 2023-12-20
 */
public class DatabaseConnectionConfig {

    public String database;
    public String address;
    public int port;
    public String username;
    public String password;

    @SuppressWarnings("unused") // For serialization
    public DatabaseConnectionConfig() {
    }

    public DatabaseConnectionConfig(String database, String address, int port, String username, String password) {
        this.database = database;
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}
