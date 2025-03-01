package com.hz6826.clockin.sql;

import io.ebean.Model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "users")
@Getter
@Setter
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

    public void addBalance(double amount) {
        this.balance += amount;
    }

    public void addRaffleTicket(int amount) {
        this.raffleTicket += amount;
    }

    public void addMakeupCard(int amount) {
        this.makeupCard += amount;
    }

    public void subtractBalance(double amount) {
        this.balance -= amount;
    }

    public void removeRaffleTicket(int amount) {
        this.raffleTicket -= amount;
    }

    public void removeMakeupCard(int amount) {
        this.makeupCard -= amount;
    }

    public boolean hasEnoughBalance(double amount) {
        return this.balance >= amount;
    }

    public boolean hasEnoughRaffleTicket(int amount) {
        return this.raffleTicket >= amount;
    }

    public boolean hasEnoughMakeupCard(int amount) {
        return this.makeupCard >= amount;
    }

    public boolean equals(@NotNull User user){
        return this.uuid.equals(user.uuid);
    }

    public void transferBalance(double amount, User toUser) {
        if (toUser == null) {
            return;
        }
        this.subtractBalance(amount);
        toUser.addBalance(amount);
    }
}