package mrnavastar.invsync.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import mrnavastar.invsync.data.entity.PlayerData;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.function.Consumer;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class ORMLite implements AutoCloseable {

    public static ORMLite connectMySQL(String database, String host, String port, String username, String password) throws Exception {
        var url = MessageFormat.format("jdbc:mysql://{1}:{2}/{0}?serverTimezone=UTC", database, host, port);
        try (var connection = new JdbcConnectionSource(url, username, password)) {
            return new ORMLite(connection);
        }
    }

    public static ORMLite connectSQLITE(String filename) throws Exception {
        var url = MessageFormat.format("jdbc:sqlite:{0}", filename);
        try (var connection = new JdbcConnectionSource(url)) {
            return new ORMLite(connection);
        }
    }

    public static ORMLite connectH2() throws Exception {
        var url = "jdbc:h2:mem:invsync";
        try (var connection = new JdbcConnectionSource(url)) {
            return new ORMLite(connection);
        }
    }

    private final ConnectionSource connection;
    public final Dao<PlayerData, String> playerDataDao;

    public ORMLite(JdbcConnectionSource connection) throws SQLException {
        this.connection = connection;
        TableUtils.createTableIfNotExists(connection, PlayerData.class);
        playerDataDao = DaoManager.createDao(connection, PlayerData.class);
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
