package com.banfly.api.domain.product.exception;

public class InactiveAccountException extends RuntimeException {
  public InactiveAccountException(String accountNumber) {
    super("Account " + accountNumber + " is not active. Current status: ");
  }

}
