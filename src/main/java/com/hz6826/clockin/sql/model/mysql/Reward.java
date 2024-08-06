package com.hz6826.clockin.sql.model.mysql;

import com.hz6826.clockin.sql.model.interfaces.RewardInterface;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Reward model for MySQL database.
 * @author OakPlatinum
 * key: unique identifier for the reward
 * key rules:
 * Daily Reward: daily_reward
 * Cumulative Reward: cumulative_reward_{days}_monthly
 * Custom Reward: custom_reward_{name}
 * @see com.hz6826.clockin.sql.model.interfaces.RewardInterface
 */
public class Reward implements RewardInterface {
    private String key;
    private String translatableKey;
    private String itemListSerialized;
    private double money;
    private int raffleTickets;
    private int makeupCards;

    public Reward(String key, String translatableKey, String itemListSerialized, double money, int raffle_tickets, int makeup_cards) {
        this.key = key;
        this.translatableKey = translatableKey;
        this.itemListSerialized = itemListSerialized;
        this.money = money;
        this.raffleTickets = raffle_tickets;
        this.makeupCards = makeup_cards;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getTranslatableKey() {
        return translatableKey;
    }

    @Override
    public String getItemListSerialized() {
        return itemListSerialized;
    }

    @Override
    public double getMoney() {
        return money;
    }

    @Override
    public int getRaffleTickets() {
        return raffleTickets;
    }

    @Override
    public int getMakeupCards() {
        return makeupCards;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void setTranslatableKey(String translatableKey) {
        this.translatableKey = translatableKey;
    }

    @Override
    public void setItemListSerialized(String itemListSerialized) {
        this.itemListSerialized = itemListSerialized;
    }

    @Override
    public void setMoney(double money) {
        this.money = money;
    }

    @Override
    public void setRaffleTickets(int raffleTickets) {
        this.raffleTickets = raffleTickets;
    }

    @Override
    public void setMakeupCards(int makeupCards) {
        this.makeupCards = makeupCards;
    }

    @Contract(pure = true)
    public static @NotNull String createTableSQL() {
        return "CREATE TABLE IF NOT EXISTS rewards (" +
                "id INT NOT NULL AUTO_INCREMENT," +
                "key VARCHAR(255) NOT NULL," +
                "translatable_key VARCHAR(255) NOT NULL," +
                "item_list_serialized TEXT," +
                "money DOUBLE," +
                "raffle_tickets INT," +
                "makeup_cards INT," +
                "PRIMARY KEY (id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
    }
}