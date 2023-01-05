package mrnavastar.invsync.data.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import mrnavastar.invsync.Config;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.apache.commons.lang3.ArrayUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
@DatabaseTable(tableName = "player")
public class PlayerData {

    @DatabaseField(id = true)
    public String playerUuid;

    @DatabaseField
    public String[] initializedServers = new String[0];

    @DatabaseField
    public Date date;

    @DatabaseField
    public NbtList inventory = new NbtList();

    @DatabaseField
    public int selectedSlot;

    @DatabaseField
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
    public NbtList effects = new NbtList();

    /*
     * JsonNull.INSTANCE should be the most clear initial value, but then things break
     */
    @DatabaseField
    public JsonElement advancements = new JsonObject();

    @SuppressWarnings("unused")
    public PlayerData() {
        // ORMLite needs a no-arg constructor
    }

    public PlayerData(UUID id) {
        playerUuid = id.toString();
    }

    public void prepareSave(Config config) {
        date = java.sql.Date.from(Instant.now());

        if (config.INITIAL_SYNC_OVERWRITE_ENABLED &&
                !Arrays.asList(initializedServers).contains(config.INITIAL_SYNC_SERVER_NAME)) {
            initializedServers = ArrayUtils.add(initializedServers, config.INITIAL_SYNC_SERVER_NAME);
        }
    }
}
