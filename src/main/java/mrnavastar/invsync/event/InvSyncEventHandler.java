package mrnavastar.invsync.event;

import mrnavastar.invsync.Config;
import mrnavastar.invsync.Logger;
import mrnavastar.invsync.data.ORMLite;
import mrnavastar.invsync.data.entity.PlayerData;
import mrnavastar.invsync.mixin_accessor.PlayerAdvancementTrackerAccessor;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.Level;

import java.util.concurrent.TimeUnit;

public class InvSyncEventHandler {

    public static void registerMinecraftEvents(ORMLite database) {
        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
            // TODO WTF?!
            try {
                // TODO WTF?!
                TimeUnit.SECONDS.sleep(1); //Maybe we can find a better solution in the future
                // TODO WTF?!
            } catch (InterruptedException e) {
                // TODO WTF?!
                throw new RuntimeException(e);
            }
            // TODO WTF?!

            Logger.log(Level.INFO, "Player JOIN event received");

            ServerPlayerEntity player = handler.getPlayer();
            database.transaction(() -> {
                try {
                    var playerData = database.playerDataDao.queryForId(player.getUuidAsString());
                    if (playerData == null)
                        playerData = new PlayerData(player.getUuid());
                    InvSyncEvents.FETCH_PLAYER_DATA.invoker().handle(player, playerData);
                    database.playerDataDao.createOrUpdate(playerData);
                    Logger.log(Level.INFO, "Player JOIN event processed");
                } catch (Exception e) {
                    Logger.logException(Level.ERROR, e);
                }
            }, e -> Logger.logException(Level.ERROR, e));
        }));

        ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
            Logger.log(Level.INFO, "Player DISCONNECT event received");

            ServerPlayerEntity player = handler.getPlayer();
            database.transaction(() -> {
                try {
                    PlayerData playerData = database.playerDataDao.queryForId(player.getUuidAsString());
                    if (playerData == null)
                        playerData = new PlayerData(player.getUuid());
                    InvSyncEvents.SAVE_PLAYER_DATA.invoker().handle(player, playerData);
                    database.playerDataDao.createOrUpdate(playerData);
                    Logger.log(Level.INFO, "Player DISCONNECT event processed");
                } catch (Exception e) {
                    Logger.logException(Level.ERROR, e);
                }
            }, e -> Logger.logException(Level.ERROR, e));
        }));
    }

    public static void registerInvSyncEvents(Config config) {
        if (config.SYNC_INVENTORY) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
                player.getInventory().readNbt(playerData.inventory);
                player.getInventory().selectedSlot = playerData.selectedSlot;
            });
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                playerData.inventory = player.getInventory().writeNbt(new NbtList());
                playerData.selectedSlot = player.getInventory().selectedSlot;
            });
        }

        if (config.SYNC_ENDER_CHEST) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.getEnderChestInventory().readNbtList(playerData.enderChest));
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.enderChest = player.getEnderChestInventory().toNbtList());
        }

        if (config.SYNC_FOOD_LEVEL) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.getHungerManager().readNbt(playerData.hunger));
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                NbtCompound nbt = new NbtCompound();
                player.getHungerManager().writeNbt(nbt);
                playerData.hunger = nbt;
            });
        }

        if (config.SYNC_HEALTH) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.setHealth(playerData.health));
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.health = player.getHealth());
        }

        if (config.SYNC_SCORE) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.setScore(playerData.score));
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.score = player.getScore());
        }

        if (config.SYNC_XP_LEVEL) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
                player.experienceLevel = playerData.xp;
                player.experienceProgress = playerData.xpProgress;
            });
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
                playerData.xp = player.experienceLevel;
                playerData.xpProgress = player.experienceProgress;
            });
        }

        if (config.SYNC_STATUS_EFFECTS) {
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

        if (false && config.SYNC_ADVANCEMENTS) {
            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> ((PlayerAdvancementTrackerAccessor) player.getAdvancementTracker()).writeAdvancementData(playerData.advancements));
            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.advancements = ((PlayerAdvancementTrackerAccessor) player.getAdvancementTracker()).readAdvancementData());
        }
    }
}
