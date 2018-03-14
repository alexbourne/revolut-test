package com.alexbourne247.revolut;

import ratpack.handling.Context;
import ratpack.handling.Handler;

import javax.inject.Inject;
import javax.inject.Singleton;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

@Singleton
public class TransferHandler implements Handler {

    private final TransferService transferService;

    @Inject
    public TransferHandler(TransferService transferService) {
        this.transferService = transferService;
    }

    @Override
    public void handle(Context context) throws Exception {

        context.parse(fromJson(TransferRequest.class))
                .then(tfr -> context.render(
                        json(transferService.transferFunds(tfr.getFromAccountId(), tfr.getToAccountId(), tfr.getAmount())))
                );

    }

}
