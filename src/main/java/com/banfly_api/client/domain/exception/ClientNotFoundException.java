package com.banfly_api.client.domain.exception;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long clientId) {
        super("Client with id " + clientId + " not found.");
    }

    public  ClientNotFoundException(String identificationNumber) {
        super("Client with identification number " + identificationNumber + " not found.");
    }
}
