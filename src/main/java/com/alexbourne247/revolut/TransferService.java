package com.alexbourne247.revolut;

public interface TransferService {

    TransferStatus transferFunds(String fromAccountId, String toAccountId, double amount);

}
