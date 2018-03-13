package com.alexbourne247.revolut;

public enum TransferStatus {

    TRANSFERRED( true ),
    INSUFFICIENT_FUNDS( false ),
    ERROR( false );

    private final boolean success;

    TransferStatus(boolean success) {
        this.success = success;
    }

    public boolean success() {
        return success;
    }

}
