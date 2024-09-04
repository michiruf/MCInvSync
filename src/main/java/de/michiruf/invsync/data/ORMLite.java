package de.michiruf.invsync.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.michiruf.invsync.Logger;
import de.michiruf.invsync.data.custom_schema.OverloadableDatabaseTableConfig;
import de.michiruf.invsync.data.entity.PlayerData;
import org.apache.logging.log4j.Level;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.function.Consumer;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class ORMLite implements AutoCloseable {

    public static ORMLite connectSQLITE(String filename, boolean debugDeleteTables) throws Exception {
        var url = MessageFormat.format("jdbc:sqlite:{0}", filename);
        try (var connection = new JdbcConnectionSource(url)) {
            return new ORMLite(connection, debugDeleteTables);
        }
    }

    public static ORMLite connect(String type, String database, String host, int port, String username, String password, boolean debugDeleteTables) throws Exception {
        var url = MessageFormat.format("jdbc:{0}://{2}:{3,number,#}/{1}?serverTimezone=UTC", type, database, host, port);
        try (var connection = new JdbcConnectionSource(url, username, password)) {
            Logger.log(Level.INFO, "Connected: " + url);
            return new ORMLite(connection, debugDeleteTables);
        }
    }

    private final ConnectionSource connection;
    public final Dao<PlayerData, String> playerDataDao;

    public ORMLite(JdbcConnectionSource connection, boolean debugDeleteTables) throws SQLException {
        this.connection = connection;

        var playerDataConfig = OverloadableDatabaseTableConfig.fromClass(
                connection.getDatabaseType(), PlayerData.class);
        if (debugDeleteTables)
            TableUtils.dropTable(connection, playerDataConfig, true);
        TableUtils.createTableIfNotExists(connection, playerDataConfig);
        playerDataDao = DaoManager.createDao(connection, playerDataConfig);
    }

    public void transaction(Runnable transaction) {
        transaction(transaction, null);
    }

    public void transaction(Runnable transaction, Consumer<SQLException> onError) {
        try {
            TransactionManager.callInTransaction(connection, () -> {
                transaction.run();
                return null;
            });
        } catch (SQLException e) {
            if (onError != null)
                onError.accept(e);
        }
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
