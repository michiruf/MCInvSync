package com.mrnavastar.invsync.conversion;

import com.mrnavastar.invsync.util.ConfigManager;
import com.mrnavastar.invsync.util.ConversionHelpers;
import com.mrnavastar.invsync.util.SQLHandler;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

public class SQLtoNBT {

    private static void convertInventory(PlayerEntity player, String uuid) {
        if (ConfigManager.Sync_Inv) {
            for (int i = 0; i < 36; i++) {
                player.inventory.main.set(i, ConversionHelpers.stringToItemStack(SQLHandler.loadString(uuid, "inv" + i)));
            }
            player.inventory.offHand.set(0, ConversionHelpers.stringToItemStack(SQLHandler.loadString(uuid, "offHand")));
            player.inventory.selectedSlot = SQLHandler.loadInt(uuid, "selectedSlot", 0);
        }
        if (ConfigManager.Sync_Armour) {
            for (int i = 0; i < 4; i++) {
                player.inventory.armor.set(i, ConversionHelpers.stringToItemStack(SQLHandler.loadString(uuid, "armour" + i)));
            }
        }
    }

    private static void convertEnderChest(PlayerEntity player, String uuid) {
        if (ConfigManager.Sync_eChest) {
            for (int i = 0; i < 27; i++) {
                player.getEnderChestInventory().setStack(i, ConversionHelpers.stringToItemStack(SQLHandler.loadString(uuid, "eChest" + i)));
            }
        }
    }

    private static void convertStats(PlayerEntity player, String uuid) {
        if (ConfigManager.Sync_Xp) {
            player.experienceLevel = SQLHandler.loadInt(uuid, "xp", 0);
            player.experienceProgress = SQLHandler.loadFloat(uuid, "xpProgress", 0);
        }
        if (ConfigManager.Sync_Score) {
            player.setScore(SQLHandler.loadInt(uuid, "score", 0));
        }
        if (ConfigManager.Sync_Health) {
            player.setHealth(SQLHandler.loadFloat(uuid, "health", 20));
        }
        if (ConfigManager.Sync_Food_Level) {
            player.getHungerManager().setFoodLevel(SQLHandler.loadInt(uuid, "foodLevel", 20));
        }
    }

    private static void convertStatusEffects(PlayerEntity player, String uuid) {

    }

    public static void convert(PlayerEntity player) {
        SQLHandler.connect();

        String uuid = player.getUuid().toString();

        convertInventory(player, uuid);
        convertEnderChest(player, uuid);
        convertStats(player, uuid);

        SQLHandler.disconnect();
    }
}