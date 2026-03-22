package com.banfly.api.domain.product.exception;

public class ClientNotFoundForProductException extends RuntimeException {

    public ClientNotFoundForProductException(Long clientId) {
        super("Cannot create product. Client with id " + clientId + " does not exist.");
    }
}
