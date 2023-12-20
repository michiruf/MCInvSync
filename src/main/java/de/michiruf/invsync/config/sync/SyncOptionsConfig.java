package de.michiruf.invsync.config.sync;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class SyncOptionsConfig {

    public boolean applySynchronizationDelay = true;
    public int synchronizationDelaySeconds = 1;
    public SynchronizationDelayMethod synchronizationDelayType = SynchronizationDelayMethod.SLEEP;

    public enum SynchronizationDelayMethod {

        SLEEP,
        TIMER
    }
}
