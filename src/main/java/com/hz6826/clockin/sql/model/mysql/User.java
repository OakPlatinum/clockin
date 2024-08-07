package com.hz6826.clockin.sql.model.mysql;

import com.hz6826.clockin.sql.model.interfaces.UserInterface;
import com.hz6826.clockin.sql.MySQLDatabaseManager;
import com.hz6826.clockin.api.economy.EconomyAccount;
import com.hz6826.clockin.sql.model.interfaces.UserWithAccountAbstract;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Environment(EnvType.SERVER)
public class User extends UserWithAccountAbstract {
    private final String uuid;
    private String playerName;
    private double balance;
    private int raffleTicket;
    private int makeupCard;

    private static final double MAX_BALANCE = 999999999999999999.99;
    private static final int MAX_RAFFLE_TICKET = 999999999;


    public User(String uuid, String playerName, double balance, int raffleTicket, int makeupCard) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.balance = balance;
        this.raffleTicket = raffleTicket;
        this.makeupCard = makeupCard;
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
    public int getMakeupCard() {
        return makeupCard;
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
    public void setMakeupCard(int makeupCard) {
        this.makeupCard = Math.max(makeupCard, 0);
        save();
    }

    @Override
    public int getBalanceRank() {
        List<UserWithAccountAbstract> users = MySQLDatabaseManager.getInstance().getUsersSortedByBalance();
        int cnt = 1;
        for (UserInterface user: users) {
            if (this.equals(user)) break;
            cnt++;
        }
        return cnt;
    }

    @Override
    public int getRaffleTicketRank() {
        List<UserWithAccountAbstract> users = MySQLDatabaseManager.getInstance().getUsersSortedByRaffleTicket();
        int cnt = 1;
        for (UserInterface user: users) {
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
    public void addMakeupCard(int amount) {
        this.setMakeupCard(this.getMakeupCard() + amount);
    }

    @Override
    public void removeMakeupCard(int amount) {
        this.setMakeupCard(this.getMakeupCard() - amount);
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
    public boolean hasMakeupCard() {
        return getMakeupCard() >= 1;
    }

    @Override
    public boolean equals(@NotNull UserInterface user){
        return this.getUuid().equals(user.getUuid());
    }

    @Contract(pure = true)
    public static @NotNull String createTableSQL() {
        return "CREATE TABLE IF NOT EXISTS users (" +
                "id INT NOT NULL AUTO_INCREMENT," +
                "uuid VARCHAR(36) NOT NULL," +
                "player_name VARCHAR(255) NOT NULL," +
                "balance DOUBLE NOT NULL DEFAULT 0," +
                "raffle_ticket INT(11) NOT NULL DEFAULT 0," +
                "makeup_card INT(11) NOT NULL DEFAULT 0," +
                "PRIMARY KEY (id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
    }
}
