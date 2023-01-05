//package mrnavastar.invsync.data;
//
//import com.google.gson.JsonElement;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import javax.persistence.Transient;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtList;
//
//import java.io.Serializable;
//import java.util.Date;
//import java.util.UUID;
//
///**
// * @author Michael Ruf
// * @since 2023-01-04
// */
//@Entity
//@Table(name = "player")
//public class PlayerData implements Serializable {
//
//    @Id
//    public String playerUuid;
//
////    @ManyToOne
////    public ServerData lastSaveServer;
//    public Date date;
//
//    @Transient
//    public NbtList inventory;
//    public int selectedSlot;
//    @Transient
//    public NbtList enderChest;
//    @Transient
//    public NbtCompound hunger;
//    public float health;
//    public int score;
//    public int xp;
//    public float xpProgress;
//    @Transient
//    public NbtList effects;
//    @Transient
//    public JsonElement advancements;
//
//    protected PlayerData() {
//    }
//
//    public PlayerData(String uuid) {
//        playerUuid = uuid;
//    }
//
//    public PlayerData(UUID id) {
//        this(id.toString());
//    }
//}
