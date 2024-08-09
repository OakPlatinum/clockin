package com.hz6826.clockin.sql.model.interfaces;

public interface EconomyAccount {
    double getBalance();
    int getRaffleTicket();

    int getMakeupCard();

    void setBalance(double balance);
    void setRaffleTicket(int raffleTicket);
    void setMakeupCard(int makeupCard);
    int getBalanceRank();
    int getRaffleTicketRank();
    void transferBalance(double amount, EconomyAccount toAccount);
    void transferRaffleTicket(int amount, EconomyAccount toAccount);
    void addBalance(double amount);
    void subtractBalance(double amount);
    void addRaffleTicket(int amount);
    void removeRaffleTicket(int amount);
    void addMakeupCard(int amount);
    void removeMakeupCard(int amount);
    boolean hasEnoughBalance(double amount);
    boolean hasEnoughRaffleTicket(int amount);
    boolean hasMakeupCard();
}
