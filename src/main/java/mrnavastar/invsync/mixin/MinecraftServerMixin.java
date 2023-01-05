package mrnavastar.invsync.mixin;

import mrnavastar.invsync.InvSync;
import mrnavastar.invsync.Logger;
import mrnavastar.invsync.data.entity.PlayerData;
import mrnavastar.invsync.event.InvSyncEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.sql.SQLException;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    public abstract PlayerManager getPlayerManager();

    @Inject(method = "saveAll", at = @At("HEAD"))
    public void onSave(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
        InvSync.database.transaction(() -> {
            getPlayerManager().getPlayerList().forEach(this::savePlayer);
        });
    }

    private void savePlayer(ServerPlayerEntity player) {
        try {
            var playerData = InvSync.database.playerDataDao.queryForId(player.getUuidAsString());
            if (playerData == null)
                playerData = new PlayerData(player.getUuid());
            InvSyncEvents.FETCH_PLAYER_DATA.invoker().handle(player, playerData);
            InvSync.database.playerDataDao.createOrUpdate(playerData);

            // ServerSyncEvents.SAVE_PLAYER_DATA.invoker().handle(player, playerData);
        } catch (SQLException e) {
            Logger.logException(Level.ERROR, e);
        }
    }
}
