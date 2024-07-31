package com.hz6826.clockin.sql.model.mysql;

import com.hz6826.clockin.sql.model.interfaces.UserInterface;
import com.hz6826.clockin.sql.MySQLDatabaseManager;

import java.sql.PreparedStatement;
import java.util.UUID;

public class User implements UserInterface {
    private int id;
    private final String uuid;
    private String playerName;
    private double balance;
    private int raffleTicket;


    public User(String uuid, String playerName, double balance, int raffleTicket) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.balance = balance;
        this.raffleTicket = raffleTicket;
    }

    public void save() {
        MySQLDatabaseManager.getInstance().updateUser(this);
    }

    public String getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public double getBalance() {
        return balance;
    }

    public int getRaffleTicket() {
        return raffleTicket;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        save();
    }

    public void setBalance(double balance) {
        this.balance = balance;
        save();
    }

    public void setRaffleTicket(int raffleTicket) {
        this.raffleTicket = raffleTicket;
        save();
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT NOT NULL AUTO_INCREMENT," +
                "uuid VARCHAR(36) NOT NULL," +
                "player_name VARCHAR(255) NOT NULL," +
                "balance DOUBLE NOT NULL DEFAULT 0," +
                "raffle_ticket INT(11) NOT NULL DEFAULT 0," +
                "PRIMARY KEY (id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        MySQLDatabaseManager.getInstance().executeUpdate(sql);
    }
}
