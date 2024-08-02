package com.hz6826.clockin.mixin;

import com.hz6826.clockin.server.ClockInServer;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hz6826.clockin.ClockIn;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
    @Unique private UserWithAccountAbstract clockinUser;

    @Unique
    public UserWithAccountAbstract getClockinUser() {
        return clockinUser;
    }
    @Unique
    public void setClockinUser(UserWithAccountAbstract clockinUser) {
        this.clockinUser = clockinUser;
    }

    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        if(ClockInServer.DATABASE_MANAGER != null){
            this.setClockinUser(ClockInServer.DATABASE_MANAGER.getOrCreateUser(player.getUuidAsString(), String.valueOf(player.getName())));
        } else {
            ClockIn.LOGGER.error("Failed to attach Clock In user.");
        }
    }
}
