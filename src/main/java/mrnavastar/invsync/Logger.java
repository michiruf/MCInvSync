package mrnavastar.invsync;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

/**
 * @author Michael Ruf
 * @since 2023-01-04
 */
public class Logger {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(InvSync.class.getSimpleName());

    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    public static void logException(Level level, Throwable throwable) {
        logger.log(level, throwable.getMessage(), throwable);
    }
}
