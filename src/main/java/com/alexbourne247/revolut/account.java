package com.alexbourne247.revolut;

public interface account {

    double getBalance(String accountId);
    boolean transferFunds(String fromAccountId, String toAccountId, double amount);

}
