package com.banfly.api.domain.client.exception;

public class ClientHasProductsException extends RuntimeException {
    public ClientHasProductsException(Long clientId) {
        super("Client with id " + clientId + " has linked products and cannot be deleted.");
    }
}
