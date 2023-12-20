package de.michiruf.invsync.config.database;

/**
 * @author Michael Ruf
 * @since 2023-12-20
 */
public class SqliteConfig {

    public String path;

    @SuppressWarnings("unused") // For serialization
    public SqliteConfig() {
    }

    public SqliteConfig(String path) {
        this.path = path;
    }
}
