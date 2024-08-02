package com.hz6826.clockin.sql.model.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public interface UserInterface{
    String getUuid();
    String getPlayerName();
    void setPlayerName(String playerName);
    boolean equals(UserInterface user);
}
