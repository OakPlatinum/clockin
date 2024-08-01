package com.hz6826.clockin.api.economy;

public interface EconomyAccount {
    double getBalance();
    int getRaffleTicket();
    void setBalance(double balance);
    void setRaffleTicket(int raffleTicket);
    int getBalanceRank();
    int getRaffleTicketRank();
    void transferBalance(double amount, EconomyAccount toAccount);
    void transferRaffleTicket(int amount, EconomyAccount toAccount);
    void addBalance(double amount);
    void subtractBalance(double amount);
    void addRaffleTicket(int amount);
    void removeRaffleTicket(int amount);
    boolean hasEnoughBalance(double amount);
    boolean hasEnoughRaffleTicket(int amount);
}
