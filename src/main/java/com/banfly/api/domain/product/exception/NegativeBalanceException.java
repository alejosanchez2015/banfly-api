package com.banfly.api.domain.product.exception;

public class NegativeBalanceException extends RuntimeException {

    public NegativeBalanceException() {
        super("Savings account balance cannot be negative.");
    }

}
