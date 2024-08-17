package de.michiruf.invsync.data.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.michiruf.invsync.config.Config;
import de.michiruf.invsync.data.custom_schema.DatabaseTypeSpecificDatabaseField;
import de.michiruf.invsync.data.custom_schema.DatabaseTypeSpecificOverload;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.ArrayUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
@DatabaseTable(tableName = "player_data")
public class PlayerData {

    @DatabaseField(id = true)
    public String playerUuid;

    @DatabaseField(width = 1000)
    public String[] initializedServers = new String[0];

    @DatabaseField
    public Date date;

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

    // JsonNull.INSTANCE should be the most clear initial value, but then things might break
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
    public PlayerData() {
        // ORMLite needs a no-arg constructor
    }

    public PlayerData(UUID id) {
        playerUuid = id.toString();
    }

    public void prepareSave(Config config) {
        date = java.sql.Date.from(Instant.now());

        if (config.initialSync.initialSyncOverwriteEnabled &&
                !Arrays.asList(initializedServers).contains(config.initialSync.initialSyncServerName)) {
            initializedServers = ArrayUtils.add(initializedServers, config.initialSync.initialSyncServerName);
        }
    }
}
