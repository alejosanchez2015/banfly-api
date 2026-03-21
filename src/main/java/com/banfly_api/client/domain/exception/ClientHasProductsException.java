package com.banfly_api.client.domain.exception;

public class ClientHasProductsException extends RuntimeException {
    public ClientHasProductsException(Long clientId) {
        super("Client with id " + clientId + " has linked products and cannot be deleted.");
    }
}
