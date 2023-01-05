package mrnavastar.invsync.event;

import mrnavastar.invsync.data.entity.PlayerData;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Michael Ruf
 * @since 2023-01-04
 */
@FunctionalInterface
public interface PlayerDataHandler {

    void handle(ServerPlayerEntity player, PlayerData playerData);
}
