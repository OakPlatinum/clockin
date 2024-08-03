package com.hz6826.clockin.mixin;

import com.hz6826.clockin.server.ClockInServer;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hz6826.clockin.ClockIn;

import java.sql.Date;
import java.time.LocalDate;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager{
    @Unique private static final Formatting CLOCKIN_INIT_MESSAGE_COLOR = Formatting.AQUA;


    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        if(ClockInServer.DATABASE_MANAGER != null){
            UserWithAccountAbstract clockInUser = ClockInServer.DATABASE_MANAGER.getOrCreateUser(player.getUuidAsString(), String.valueOf(player.getName()));
            player.sendMessage(Text.translatable("command.clockin.init.headline").formatted(CLOCKIN_INIT_MESSAGE_COLOR));
            player.sendMessage(Text.translatable("command.clockin.init.headline2").formatted(CLOCKIN_INIT_MESSAGE_COLOR));
            player.sendMessage(Text.translatable("command.clockin.init.welcome", player.getName()).formatted(CLOCKIN_INIT_MESSAGE_COLOR));
            Text balanceText = Text.literal(String.valueOf(clockInUser.getBalance())).formatted(Formatting.GOLD);
            Text rankText = Text.literal(String.valueOf(clockInUser.getBalanceRank())).formatted(Formatting.GOLD);
            player.sendMessage(Text.translatable("command.clockin.init.balance", balanceText, rankText));
            if(ClockInServer.DATABASE_MANAGER.getDailyClockInRecordOrNull(player.getUuidAsString(), Date.valueOf(LocalDate.now())) == null) {
                Text clockInButton = Text.translatable("command.clockin.init.clockin.button").styled(style -> style
                        .withColor(Formatting.AQUA) // 设置文本颜色
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clockin clockin"))
                );
                player.sendMessage(Text.translatable("command.clockin.init.clockin.require", clockInButton));
            }
            player.sendMessage(Text.translatable("command.clockin.init.foot"));
        } else {
            ClockIn.LOGGER.error("Failed to attach Clock In user.");
        }
    }
}
