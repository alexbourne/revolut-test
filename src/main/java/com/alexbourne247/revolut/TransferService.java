package com.alexbourne247.revolut;

public interface TransferService {

    /**
     * Transfer funds from one account to another
     *
     * @param fromAccountId The account to transfer funds from
     * @param toAccountId   The account to transfer funds to
     * @param amount of money to transfer
     * @return {@link TransferStatus} depending on outcome of transfer
     */
    TransferStatus transferFunds(int fromAccountId, int toAccountId, double amount);

}
