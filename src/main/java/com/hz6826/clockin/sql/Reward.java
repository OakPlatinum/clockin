package com.hz6826.clockin.sql;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.Length;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity(name = "rewards")
public class Reward extends Model {

    @Id
    private long id; // 对应 AUTO_INCREMENT 列，作为主键

    @Column(unique = true, nullable = false)
    private String key;

    @Column(name="translatable_key", nullable = false)
    private String translatableKey;

    @Column(name="item_list_serialized", nullable = false)
    private String itemListSerialized;

    @Column(name="money", nullable = false)
    private double money = 0;

    @Column(name="raffle_tickets", nullable = false)
    private int raffleTickets;

    @Column(name="makeup_cards", nullable = false)
    private int makeupCards;

    // 无参构造方法（推荐）
    public Reward() {
    }

    public Reward(long id,
                  String key,
                  String translatableKey,
                  String itemListSerialized,
                  double money,
                  int raffleTickets,
                  int makeupCards) {
        this.id = id;
        this.key = key;
        this.translatableKey = translatableKey;
        this.itemListSerialized = itemListSerialized;
        this.money = money;
        this.raffleTickets = raffleTickets;
        this.makeupCards = makeupCards;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTranslatableKey() {
        return translatableKey;
    }

    public void setTranslatableKey(String translatableKey) {
        this.translatableKey = translatableKey;
    }

    public String getItemListSerialized() {
        return itemListSerialized;
    }

    public void setItemListSerialized(String itemListSerialized) {
        this.itemListSerialized = itemListSerialized;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getRaffleTickets() {
        return raffleTickets;
    }

    public void setRaffleTickets(int raffleTickets) {
        this.raffleTickets = raffleTickets;
    }

    public int getMakeupCards() {
        return makeupCards;
    }

    public void setMakeupCards(int makeupCards) {
        this.makeupCards = makeupCards;
    }

}
