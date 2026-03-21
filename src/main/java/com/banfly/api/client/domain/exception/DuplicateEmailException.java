package com.banfly.api.client.domain.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("A client with email " + email + " already exists.");
    }
}

