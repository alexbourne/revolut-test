package com.alexbourne247.revolut;

public class BankSystem implements TransferService {

    @Override
    public TransferStatus transferFunds(String fromAccountId, String toAccountId, double amount) {
        return TransferStatus.ERROR;
    }
}
