package com.alexbourne247.revolut;

public interface TransferService {

    /**
     * Transfer funds from one account to another
     *
     * @param fromAccountId
     * @param toAccountId
     * @param amount of money to transfer
     * @return status of transfer
     */
    TransferStatus transferFunds(int fromAccountId, int toAccountId, double amount);

}
