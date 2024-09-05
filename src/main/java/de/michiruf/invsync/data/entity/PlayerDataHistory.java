package de.michiruf.invsync.data.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.michiruf.invsync.config.Config;
import de.michiruf.invsync.data.custom_schema.DatabaseTypeSpecificDatabaseField;
import de.michiruf.invsync.data.custom_schema.DatabaseTypeSpecificOverload;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "player_data_history")
public class PlayerDataHistory {
    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField
    public String playerUuid;

    @DatabaseField
    public String playerUsername;

    @DatabaseField(width = 1000)
    public String[] initializedServers = new String[0];

    @DatabaseField
    public Date date;

    @DatabaseField
    public Date creationDate;

    @DatabaseField
    @DatabaseTypeSpecificDatabaseField({
            @DatabaseTypeSpecificOverload(
                    typeName = "MySQL",
                    databaseField = @DatabaseField(columnDefinition = "LONGTEXT")
            )
    })
    public NbtList inventory = new NbtList();

    @DatabaseField
    public int selectedSlot;

    @DatabaseField
    @DatabaseTypeSpecificDatabaseField({
            @DatabaseTypeSpecificOverload(
                    typeName = "MySQL",
                    databaseField = @DatabaseField(columnDefinition = "LONGTEXT")
            )
    })
    public NbtList enderChest = new NbtList();

    @DatabaseField
    public NbtCompound hunger = new NbtCompound();

    @DatabaseField
    public float health;

    @DatabaseField
    public int score;

    @DatabaseField
    public int xp;

    @DatabaseField
    public float xpProgress;

    @DatabaseField
    @DatabaseTypeSpecificDatabaseField({
            @DatabaseTypeSpecificOverload(
                    typeName = "MySQL",
                    databaseField = @DatabaseField(columnDefinition = "LONGTEXT")
            )
    })
    public NbtList effects = new NbtList();

    @DatabaseField
    @DatabaseTypeSpecificDatabaseField({
            @DatabaseTypeSpecificOverload(
                    typeName = "MySQL",
                    databaseField = @DatabaseField(columnDefinition = "LONGTEXT")
            )
    })
    public JsonElement advancements = new JsonObject();


    @DatabaseField
    @DatabaseTypeSpecificDatabaseField({
            @DatabaseTypeSpecificOverload(
                    typeName = "MySQL",
                    databaseField = @DatabaseField(columnDefinition = "LONGTEXT")
            )
    })
    public JsonElement trinkets = new JsonObject();

    @SuppressWarnings("unused")
    public PlayerDataHistory() {
        // ORMLite needs a no-arg constructor
    }

    public PlayerDataHistory(UUID id, Date date) {
        playerUuid = id.toString();
        this.creationDate = date;
    }

    public void prepareSave(Config config) {
        date = java.sql.Date.from(Instant.now());
    }
}
