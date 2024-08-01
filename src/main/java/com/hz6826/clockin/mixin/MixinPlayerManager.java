package com.hz6826.clockin.mixin;

import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

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

}
