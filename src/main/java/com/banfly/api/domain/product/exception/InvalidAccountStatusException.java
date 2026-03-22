package com.banfly.api.domain.product.exception;

import com.banfly.api.domain.product.model.AccountStatus;

public class InvalidAccountStatusException extends RuntimeException {

    public InvalidAccountStatusException(AccountStatus status) {
        super("Transition to status " + status + " is not allowed.");
    }
}
