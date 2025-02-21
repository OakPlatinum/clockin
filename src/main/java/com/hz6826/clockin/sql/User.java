package com.hz6826.clockin.sql;

import io.ebean.Model;
import jakarta.persistence.Column;
import jakarta.persistence.Id;


public class User extends Model {
    @Id
    private int id;

    @Column(nullable = false, length = 36)
    private String uuid;

    @Column(name="player_name", nullable = false)
    private String playerName;

    @Column(nullable = false)
    private double balance = 0;

    @Column(name="raffle_ticket", nullable = false)
    private int raffleTicket = 0;

    @Column(name="makeup_card", nullable = false)
    private int makeupCard = 0;

    public User() {
    }

    // 带参构造函数（可选）
    public User(String uuid, String playerName, double balance, int raffleTicket, int makeupCard) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.balance = balance;
        this.raffleTicket = raffleTicket;
        this.makeupCard = makeupCard;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public double getBalance() {
        return balance;
    }

    public User setBalance(double balance) {
        this.balance = balance;
        return this;
    }

    public int getRaffleTicket() {
        return raffleTicket;
    }

    public void setRaffleTicket(int raffleTicket) {
        this.raffleTicket = raffleTicket;
    }

    public int getMakeupCard() {
        return makeupCard;
    }

    public void setMakeupCard(int makeupCard) {
        this.makeupCard = makeupCard;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", playerName='" + playerName + '\'' +
                ", balance=" + balance +
                ", raffleTicket=" + raffleTicket +
                ", makeupCard=" + makeupCard +
                '}';
    }
}