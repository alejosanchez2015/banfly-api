package com.banfly.api.domain.client.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("A client with email " + email + " already exists.");
    }
}

