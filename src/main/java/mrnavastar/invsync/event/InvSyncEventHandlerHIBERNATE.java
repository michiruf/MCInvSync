//package mrnavastar.invsync.event;
//
//import mrnavastar.invsync.Config;
//import mrnavastar.invsync.data.PlayerData;
//import mrnavastar.invsync.mixinextension.PlayerAdvancementTrackerAccessor;
//import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
//import net.minecraft.entity.effect.StatusEffectInstance;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
//import net.minecraft.nbt.NbtList;
//import net.minecraft.server.network.ServerPlayerEntity;
//import org.hibernate.SessionFactory;
//
//import java.util.concurrent.TimeUnit;
//
//public class InvSyncEventHandler {
//
//    public static void registerMinecraftEvents(SessionFactory sessionFactory) {
//        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
//            try {
//                // TODO WTF?!
//                TimeUnit.SECONDS.sleep(1); //Maybe we can find a better solution in the future
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//            ServerPlayerEntity player = handler.getPlayer();
//
//            try (var session = sessionFactory.openSession()) {
//                var transaction = session.beginTransaction();
//
//                var playerData = session.get(PlayerData.class, player.getUuidAsString());
//                InvSyncEvents.FETCH_PLAYER_DATA.invoker().handle(player, playerData);
//
//                transaction.commit();
//            }
//        }));
//
//        ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
//            ServerPlayerEntity player = handler.getPlayer();
//
//            try (var session = sessionFactory.openSession()) {
//                var transaction = session.beginTransaction();
//
//                var playerInventory = session.get(PlayerData.class, player.getUuidAsString());
//                if (playerInventory == null)
//                    playerInventory = new PlayerData(player.getUuid());
//                InvSyncEvents.SAVE_PLAYER_DATA.invoker().handle(player, playerInventory);
//                session.persist(playerInventory);
//
//                transaction.commit();
//            }
//        }));
//    }
//
//    public static void registerInvSyncEvents(Config config) {
//        if (config.SYNC_INVENTORY) {
//            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
//                player.getInventory().readNbt(playerData.inventory);
//                player.getInventory().selectedSlot = playerData.selectedSlot;
//            });
//            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
//                playerData.inventory = player.getInventory().writeNbt(new NbtList());
//                playerData.selectedSlot = player.getInventory().selectedSlot;
//            });
//        }
//
//        if (config.SYNC_ENDER_CHEST) {
//            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.getEnderChestInventory().readNbtList(playerData.enderChest));
//            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.enderChest = player.getEnderChestInventory().toNbtList());
//        }
//
//        if (config.SYNC_FOOD_LEVEL) {
//            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.getHungerManager().readNbt(playerData.hunger));
//            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
//                NbtCompound nbt = new NbtCompound();
//                player.getHungerManager().writeNbt(nbt);
//                playerData.hunger = nbt;
//            });
//        }
//
//        if (config.SYNC_HEALTH) {
//            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.setHealth(playerData.health));
//            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.health = player.getHealth());
//        }
//
//        if (config.SYNC_SCORE) {
//            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> player.setScore(playerData.score));
//            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.score = player.getScore());
//        }
//
//        if (config.SYNC_XP_LEVEL) {
//            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
//                player.experienceLevel = playerData.xp;
//                player.experienceProgress = playerData.xpProgress;
//            });
//            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
//                playerData.xp = player.experienceLevel;
//                playerData.xpProgress = player.experienceProgress;
//            });
//        }
//
//        if (config.SYNC_STATUS_EFFECTS) {
//            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> {
//                NbtList effects = playerData.effects;
//                if (effects != null) {
//                    player.clearStatusEffects();
//                    for (NbtElement effect : effects)
//                        player.addStatusEffect(StatusEffectInstance.fromNbt((NbtCompound) effect));
//                }
//            });
//            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> {
//                NbtList effects = new NbtList();
//                for (StatusEffectInstance effect : player.getStatusEffects())
//                    effects.add(effect.writeNbt(new NbtCompound()));
//                playerData.effects = effects;
//            });
//        }
//
//        if (config.SYNC_ADVANCEMENTS) {
//            InvSyncEvents.FETCH_PLAYER_DATA.register((player, playerData) -> ((PlayerAdvancementTrackerAccessor) player.getAdvancementTracker()).writeAdvancementData(playerData.advancements));
//            InvSyncEvents.SAVE_PLAYER_DATA.register((player, playerData) -> playerData.advancements = ((PlayerAdvancementTrackerAccessor) player.getAdvancementTracker()).readAdvancementData());
//        }
//    }
//}
