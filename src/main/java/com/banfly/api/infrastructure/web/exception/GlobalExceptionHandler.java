package com.banfly.api.infrastructure.web.exception;

import com.banfly.api.domain.client.exception.ClientHasProductsException;
import com.banfly.api.domain.client.exception.ClientNotFoundException;
import com.banfly.api.domain.client.exception.DuplicateEmailException;
import com.banfly.api.domain.client.exception.UnderageClientException;
import com.banfly.api.domain.client.model.IdentificationType;
import com.banfly.api.domain.product.exception.*;
import com.banfly.api.domain.product.model.AccountStatus;
import com.banfly.api.domain.product.model.AccountType;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ClientNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UnderageClientException.class)
    public ResponseEntity<ErrorResponse> handleUnderage(UnderageClientException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ClientHasProductsException.class)
    public ResponseEntity<ErrorResponse> handleHasProducts(ClientHasProductsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMenu(HttpMessageNotReadableException ex) {
        String message = "Invalid request body. Please verify the values sent.";

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException invalidFormat) {
            Class<?> targetType = invalidFormat.getTargetType();

            if (targetType.equals(IdentificationType.class)) {
                message = "Invalid identification type. Allowed values: CC, CE, NIT, PP";
            } else if (targetType.equals(AccountType.class)) {
                message = "Invalid account type. Allowed values: AHORROS, CORRIENTE";
            } else if (targetType.equals(AccountStatus.class)) {
                message = "Invalid account status. Allowed values: ACTIVA, INACTIVA, CANCELADA";
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));

    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(NegativeBalanceException.class)
    public ResponseEntity<ErrorResponse> handleNegativeBalance(NegativeBalanceException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(AccountCancellationException.class)
    public ResponseEntity<ErrorResponse> handleCancellation(AccountCancellationException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidAccountStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatus(InvalidAccountStatusException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ClientNotFoundForProductException.class)
    public ResponseEntity<ErrorResponse> handleClientNotFoundForProduct(ClientNotFoundForProductException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<ErrorResponse> handleInactiveAccount(InactiveAccountException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(ex.getMessage()));
    }


}
