package com.banfly.api.domain.transaction.exception;

public class SameAccountTransferException extends RuntimeException {
    public SameAccountTransferException() {
        super("Transfer source and target accounts must be different.");
    }
}
