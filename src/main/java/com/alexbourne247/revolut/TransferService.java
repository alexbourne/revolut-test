package com.alexbourne247.revolut;

public interface TransferService {

    TransferStatus transferFunds(int fromAccountId, int toAccountId, double amount);

}
