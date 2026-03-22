package com.banfly.api.domain.product.exception;

public class AccountCancellationException extends RuntimeException {

    public AccountCancellationException() {
        super("Account can only be cancelled when balance is zero.");
    }

}
