//package mrnavastar.invsync.data;
//
//import mrnavastar.invsync.InvSync;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//import org.hibernate.cfg.Environment;
//
//import java.text.MessageFormat;
//import java.util.Properties;
//
///**
// * @author Michael Ruf
// * @since 2023-01-04
// */
//public class Hibernate6Util {
//
//    public static SessionFactory getSessionFactory(Properties properties) {
//        return new Configuration()
//                .setProperties(properties)
//                .addPackage(InvSync.class.getPackageName())
//                .buildSessionFactory();
//    }
//
//    public static Properties mysqlProperties(String database, String host, String port, String username, String password) {
//        var properties = new Properties();
//        properties.setProperty(Environment.DRIVER, "com.mysql.jdbc.Driver");
//        properties.setProperty(Environment.URL, MessageFormat.format("jdbc:mysql://{1}:{2}/{0}?serverTimezone=UTC", database, host, port));
//        properties.setProperty(Environment.USER, username);
//        properties.setProperty(Environment.PASS, password);
//        properties.setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
//        properties.setProperty(Environment.HBM2DDL_AUTO, "create");
//        properties.setProperty(Environment.SHOW_SQL, "true");
//        return properties;
//    }
//
//    public static Properties sqliteProperties(String filename) {
//        System.err.println("SQLite currently not supported");
//        System.exit(-1);
//
//        var properties = new Properties();
//        properties.setProperty(Environment.DRIVER, "org.sqlite.JDBC");
//        properties.setProperty(Environment.URL, MessageFormat.format("jdbc:sqlite:{0}", filename));
//        properties.setProperty(Environment.DIALECT, "org.hibernate.community.dialect.SQLiteDialect");
//        properties.setProperty(Environment.DIALECT_RESOLVERS, "org.hibernate.community.dialect.CommunityDialectResolver");
//        properties.setProperty(Environment.HBM2DDL_AUTO, "create");
//        properties.setProperty(Environment.SHOW_SQL, "true");
//        return properties;
//    }
//
//    public static Properties h2Properties() {
//        var properties = new Properties();
//        properties.setProperty(Environment.DRIVER, "org.h2.Driver");
//        properties.setProperty(Environment.URL, "jdbc:h2:mem:invsync");
//        properties.setProperty(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
//        properties.setProperty(Environment.HBM2DDL_AUTO, "create");
//        properties.setProperty(Environment.SHOW_SQL, "true");
//        return properties;
//    }
//}
