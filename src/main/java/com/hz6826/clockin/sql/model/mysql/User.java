package com.hz6826.clockin.sql.model.mysql;

import com.hz6826.clockin.sql.model.interfaces.UserInterface;
import com.hz6826.clockin.sql.MySQLDatabaseManager;
import com.hz6826.clockin.api.economy.EconomyAccount;

import java.util.List;

public class User implements UserInterface, EconomyAccount {
    private int id;
    private final String uuid;
    private String playerName;
    private double balance;
    private int raffleTicket;

    private static final double MAX_BALANCE = 999999999999999999.99;
    private static final int MAX_RAFFLE_TICKET = 999999999;


    public User(String uuid, String playerName, double balance, int raffleTicket) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.balance = balance;
        this.raffleTicket = raffleTicket;
    }

    public User(int id, String uuid, String playerName, double balance, int raffleTicket) {
        this.id = id;
        this.uuid = uuid;
        this.playerName = playerName;
        this.balance = balance;
        this.raffleTicket = raffleTicket;
    }

    public void save() {
        MySQLDatabaseManager.getInstance().updateUser(this);
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public int getRaffleTicket() {
        return raffleTicket;
    }

    @Override
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        save();
    }

    @Override
    public void setBalance(double balance) {
        if (balance < 0) {
            this.balance = 0;
        } else if (balance > MAX_BALANCE) {
            this.balance = MAX_BALANCE;
        } else {
            this.balance = Math.round(balance * 100.0) / 100.0;
        }
        save();
    }

    @Override
    public void setRaffleTicket(int raffleTicket) {
        if (raffleTicket < 0) {
            this.raffleTicket = 0;
        } else this.raffleTicket = Math.min(raffleTicket, MAX_RAFFLE_TICKET);
        save();
    }

    @Override
    public int getBalanceRank() {
        List<User> users = MySQLDatabaseManager.getInstance().getUsersSortedByBalance();
        int cnt = 1;
        for (User user: users) {
            if (this.equals(user)) break;
            cnt++;
        }
        return cnt;
    }

    @Override
    public int getRaffleTicketRank() {
        List<User> users = MySQLDatabaseManager.getInstance().getUsersSortedByRaffleTicket();
        int cnt = 1;
        for (User user: users) {
            if (this.equals(user)) break;
            cnt++;
        }
        return cnt;
    }

    @Override
    public void transferBalance(double amount, EconomyAccount toAccount) {
        if (toAccount == null) {
            return;
        }
        this.subtractBalance(amount);
        toAccount.addBalance(amount);
    }

    @Override
    public void transferRaffleTicket(int amount, EconomyAccount toAccount) {
        if (toAccount == null) {
            return;
        }
        this.removeRaffleTicket(amount);
        toAccount.addRaffleTicket(amount);
    }

    @Override
    public void addBalance(double amount) {
        this.setBalance(this.getBalance() + amount);
    }

    @Override
    public void subtractBalance(double amount) {
        this.setBalance(this.getBalance() - amount);
    }

    @Override
    public void addRaffleTicket(int amount) {
        this.setRaffleTicket(this.getRaffleTicket() + amount);
    }

    @Override
    public void removeRaffleTicket(int amount) {
        this.setRaffleTicket(this.getRaffleTicket() - amount);
    }

    @Override
    public boolean hasEnoughBalance(double amount) {
        return this.getBalance() >= amount;
    }

    @Override
    public boolean hasEnoughRaffleTicket(int amount) {
        return this.getRaffleTicket() >= amount;
    }

    @Override
    public boolean equals(UserInterface user){
        return this.getUuid().equals(user.getUuid());
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
