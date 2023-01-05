package mrnavastar.invsync.data.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

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
    public String[] initializedServers;

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

    @DatabaseField
    public JsonElement advancements = new JsonObject();

    @SuppressWarnings("unused")
    public PlayerData() {
        // ORMLite needs a no-arg constructor
    }

    public PlayerData(UUID id) {
        playerUuid = id.toString();
    }
}
