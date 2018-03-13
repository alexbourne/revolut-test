package com.alexbourne247.revolut;

import com.google.inject.AbstractModule;

public class TransferModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TransferService.class).to(BankSystem.class);
        bind(TransferHandler.class);
    }

}
