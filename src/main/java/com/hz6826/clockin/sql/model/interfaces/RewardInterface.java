package com.hz6826.clockin.sql.model.interfaces;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface RewardInterface {
    String getKey();

    String getTranslatableKey();

    String getItemListSerialized();

    double getMoney();

    int getRaffleTickets();

    int getMakeupCards();

    void setKey(String key);

    void setTranslatableKey(String translatableKey);

    void setItemListSerialized(String itemListSerialized);

    void setMoney(double money);

    void setRaffleTickets(int raffleTickets);

    void setMakeupCards(int makeupCards);
}
