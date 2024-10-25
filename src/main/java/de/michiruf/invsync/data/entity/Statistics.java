package de.michiruf.invsync.data.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.michiruf.invsync.config.Config;

import java.time.Instant;
import java.util.Date;

@DatabaseTable(tableName = "statistics")
public class Statistics {

    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField
    public String serverName;

    @DatabaseField
    public int playersOnline;

    @DatabaseField
    public double serverTick;

    @DatabaseField
    public long entityCount;

    @DatabaseField
    public double uptimeSeconds;

    @DatabaseField
    public long loadedChunks;

    @DatabaseField
    public double memoryUsage;

    @DatabaseField
    public double maxMemory;

    @DatabaseField
    public double averagePlayerPing;

    @DatabaseField
    public double averagePlayerLevel;

    @DatabaseField
    public double averagePlayerDeathCount;

    @DatabaseField
    public Date date;

    @SuppressWarnings("unused")
    public Statistics() {
        // ORMLite needs a no-arg constructor
    }

    public Statistics(Date date) {
        this.date = date;
    }

    public void prepareSave(Config config) {
        date = java.sql.Date.from(Instant.now());
    }
}
