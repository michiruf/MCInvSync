package de.michiruf.invsync.event;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import de.michiruf.invsync.Logger;
import de.michiruf.invsync.config.Config;
import de.michiruf.invsync.mixin_accessor.PlayerAdvancementTrackerAccessor;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix.GSON;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class InvSyncEventsHandler {

    /**
     * @param config Plugin configuration
     * @see net.minecraft.entity.player.PlayerEntity#readCustomDataFromNbt(NbtCompound)
     * @see net.minecraft.server.PlayerManager#createPlayer(GameProfile, PlayerPublicKey)
     */
    public static void registerEvents(Config config) {

        final String DATA_VERSION_PROPERTY = "DataVersion";

        // Synchronize achievements first for them not to get triggered by inventory updates
        // Of cause this just registers the event, and does not specify the execution order explicitly
        // but registering this first might already help
        if (config.sync.advancements) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> ((PlayerAdvancementTrackerAccessor) player.getAdvancementTracker()).writeAdvancementData(playerData.advancements));
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.advancements = ((PlayerAdvancementTrackerAccessor) player.getAdvancementTracker()).readAdvancementData());
        }

        if (config.sync.inventory) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
                player.getInventory().readNbt(playerData.inventory);
                player.getInventory().selectedSlot = playerData.selectedSlot;
                TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
                    Logger.log(Level.INFO, "Loading trinkets");
                    // Delete all existing trinkets? maybe not needed
                    trinkets.getInventory().clear();
                    Type type = new TypeToken<Map<String, JsonObject>>(){}.getType();
                    Map<String, JsonObject> map = GSON.fromJson(playerData.trinkets, type);
                    if (map != null) {
                        map.forEach((key, value) -> {
                            String[] split = key.split("/");
                            String group = split[0];
                            String slot = split[1];
                            int index = Integer.parseInt(split[2]);
                            Map<String, TrinketInventory> slots = trinkets.getInventory().get(group);
                            if (slots != null) {
                                TrinketInventory inv = slots.get(slot);
                                if (inv != null && index < inv.size()) {
                                    inv.setStack(index, jsonToStack(value));
                                }
                            }
                        });
                    } else {
                        Logger.log(Level.INFO, "Could not load trinkets, invalid or null json");
                    }
                });
            });
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                playerData.inventory = player.getInventory().writeNbt(new NbtList());
                playerData.selectedSlot = player.getInventory().selectedSlot;
                Logger.log(Level.INFO, "Saving trinkets");
                TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
                    Logger.log(Level.INFO, "Loop All trinkets");
                    Map<String, JsonObject> toSave = Maps.newHashMap();
                    trinkets.forEach((ref, stack) -> {
                        TrinketInventory inventory = ref.inventory();
                        SlotType slotType = inventory.getSlotType();
                        int index = ref.index();
                        String newRef = slotType.getGroup() + "/" + slotType.getName() + "/" + index;
                        if (stack.getCount() > 0) {
                            Logger.log(Level.INFO, "Found: " + newRef);
                            toSave.put(newRef, stackToJson(stack));
                        }
                    });
                    playerData.trinkets = GSON.toJsonTree(toSave);
                });
            });
        }

        if (config.sync.enderChest) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.getEnderChestInventory().readNbtList(playerData.enderChest));
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.enderChest = player.getEnderChestInventory().toNbtList());
        }

        if (config.sync.foodLevel) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.getHungerManager().readNbt(playerData.hunger));
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                NbtCompound nbt = new NbtCompound();
                player.getHungerManager().writeNbt(nbt);
                playerData.hunger = nbt;
            });
        }

        if (config.sync.health) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.setHealth(playerData.health));
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.health = player.getHealth());
        }

        if (config.sync.score) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.setScore(playerData.score));
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.score = player.getScore());
        }

        if (config.sync.xpLevel) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
                player.experienceLevel = playerData.xp;
                player.experienceProgress = playerData.xpProgress;
                // Add 0 experience to trigger a synchronization to the player and also clamp the value
                player.addExperience(0);
            });
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                playerData.xp = player.experienceLevel;
                playerData.xpProgress = player.experienceProgress;
            });
        }

        if (config.sync.statusEffects) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
                NbtList effects = playerData.effects;
                if (effects != null) {
                    player.clearStatusEffects();
                    for (NbtElement effect : effects)
                        player.addStatusEffect(StatusEffectInstance.fromNbt((NbtCompound) effect));
                }
            });
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                NbtList effects = new NbtList();
                for (StatusEffectInstance effect : player.getStatusEffects())
                    effects.add(effect.writeNbt(new NbtCompound()));
                playerData.effects = effects;
            });
        }
    }

    static JsonObject stackToJson(ItemStack stack) {
        JsonObject obj = new JsonObject();
        obj.addProperty("item", stack.writeNbt(new NbtCompound()).toString());
        return obj;
    }

    static ItemStack jsonToStack(JsonObject obj) {
        final String item = JsonHelper.getString(obj, "item");
        Logger.log(Level.INFO, "Decoding: " + item);
        try {
            NbtCompound nbt = StringNbtReader.parse(item);
            Logger.log(Level.INFO, "Decoded: " + nbt.toString());
            ItemStack itemStack = ItemStack.fromNbt(nbt);
            Logger.log(Level.INFO, "Found: " + itemStack.toString());
            return itemStack;
        } catch (CommandSyntaxException e) {
            return ItemStack.EMPTY;
        }
    }
}
