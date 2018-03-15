package com.alexbourne247.revolut;

import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

public class TransferApp {

    /*
        A simple demonstration web service app that enables transferring money
        from one account to another using an in memory database
     */
    public static void main(String[] args) throws Exception {

        RatpackServer.start( server -> server
            .registry(Guice.registry( bindings -> bindings
                .module(TransferModule.class)
            ))
            .handlers(chain -> chain
                    .get(ctx -> ctx.render("Account Transfer Service"))
                    .path("transfer", TransferHandler.class)
            )
        );
    }
}
