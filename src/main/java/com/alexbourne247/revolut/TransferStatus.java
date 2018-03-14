package com.alexbourne247.revolut;

public enum TransferStatus {

    TRANSFERRED( true ),
    INSUFFICIENT_FUNDS( false ),
    FROM_ACCOUNT_DOESNT_EXIST( false ),
    TO_ACCOUNT_DOESNT_EXIST( false ),
    INVALID_TRANSFER_AMOUNT( false ),
    ERROR( false );

    private final boolean success;

    TransferStatus(boolean success) {
        this.success = success;
    }

    public boolean success() {
        return success;
    }

}
