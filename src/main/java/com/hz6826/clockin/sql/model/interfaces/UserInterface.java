package com.hz6826.clockin.sql.model.interfaces;

public interface UserInterface{
    String getUuid();
    String getPlayerName();
    void setPlayerName(String playerName);
    boolean equals(UserInterface user);
}
