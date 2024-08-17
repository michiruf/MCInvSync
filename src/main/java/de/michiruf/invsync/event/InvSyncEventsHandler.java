package de.michiruf.invsync.event;

import com.mojang.authlib.GameProfile;
import de.michiruf.invsync.Logger;
import de.michiruf.invsync.config.Config;
import de.michiruf.invsync.mixin_accessor.PlayerAdvancementTrackerAccessor;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.apache.logging.log4j.Level;

import java.util.Optional;

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
                Optional<TrinketComponent> tc = TrinketsApi.getTrinketComponent(player);
                if (tc.isPresent() && playerData.trinkets != null) {
                    tc.get().getAllEquipped().clear();
                    tc.get().getAllEquipped().addAll(playerData.trinkets);
                }
            });
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                playerData.inventory = player.getInventory().writeNbt(new NbtList());
                playerData.selectedSlot = player.getInventory().selectedSlot;
                Optional<TrinketComponent> tc = TrinketsApi.getTrinketComponent(player);
                Logger.log(Level.INFO, "Saving trinkets");
                if (tc.isPresent()) {
                    Logger.log(Level.INFO, "Get All Equipped trinkets");
                    playerData.trinkets = tc.get().getAllEquipped();
                    if (playerData.trinkets != null) {
                        Logger.log(Level.INFO, "Found: " + playerData.trinkets);
                    }
                } else {
                    Logger.log(Level.INFO, "Trinket component not present");
                }
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
}
