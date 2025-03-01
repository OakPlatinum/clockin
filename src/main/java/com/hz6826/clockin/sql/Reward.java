package com.hz6826.clockin.sql;

import io.ebean.Model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "rewards")
@Getter
@Setter
public class Reward extends Model {

    @Id
    private long id; // 对应 AUTO_INCREMENT 列，作为主键

    @Column(name="`key`", unique = true, nullable = false)
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

    public Reward(String key,
                  String translatableKey,
                  String itemListSerialized,
                  double money,
                  int raffleTickets,
                  int makeupCards) {
        this.key = key;
        this.translatableKey = translatableKey;
        this.itemListSerialized = itemListSerialized;
        this.money = money;
        this.raffleTickets = raffleTickets;
        this.makeupCards = makeupCards;
    }

    public Reward(String key) {
        this.key = key;
    }

}
