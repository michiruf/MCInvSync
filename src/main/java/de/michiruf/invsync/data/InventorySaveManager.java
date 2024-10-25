package de.michiruf.invsync.data;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventorySaveManager {
    // Map to track players who should not have their inventory saved
    private static final Map<UUID, Boolean> disableInventorySaveMap = new ConcurrentHashMap<>();

    /**
     * Disables inventory saving for a specific player.
     *
     * @param player The player for whom inventory saving should be disabled.
     */
    public static void disableInventorySave(ServerPlayerEntity player) {
        disableInventorySaveMap.put(player.getUuid(), true);
    }

    /**
     * Checks whether inventory should be saved for a specific player.
     *
     * @param player The player to check.
     * @return True if inventory should be saved, false otherwise.
     */
    public static boolean shouldSaveInventory(ServerPlayerEntity player) {
        return !disableInventorySaveMap.getOrDefault(player.getUuid(), false);
    }

    /**
     * Cleans up the flag for a player when they disconnect.
     *
     * @param player The player who has disconnected.
     */
    public static void removePlayerFlag(ServerPlayerEntity player) {
        disableInventorySaveMap.remove(player.getUuid());
    }
}
