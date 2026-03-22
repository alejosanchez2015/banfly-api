package com.banfly.api.domain.product.exception;

public class ProductNotFoundException extends RuntimeException{

    public ProductNotFoundException(Long id) {
        super("Product with id " + id + " not found.");
    }

    public ProductNotFoundException(String accountNumber) {
        super("Product with account number " + accountNumber + " not found.");
    }
}
