package com.banfly.api.infrastructure.web.controller;

import com.banfly.api.domain.transaction.core.in.TransactionUseCase;
import com.banfly.api.domain.transaction.model.Transaction;
import com.banfly.api.infrastructure.web.dto.request.DepositRequest;
import com.banfly.api.infrastructure.web.dto.request.TransferRequest;
import com.banfly.api.infrastructure.web.dto.request.WithdrawalRequest;
import com.banfly.api.infrastructure.web.dto.response.TransactionResponse;
import com.banfly.api.infrastructure.web.mapper.TransactionDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Financial transaction endpoints")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionUseCase transactionUseCase;
    private final TransactionDtoMapper mapper;

    @Operation(summary = "Process a deposit")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Deposit processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "422", description = "Account is not active")
    })
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody DepositRequest request) {
        Transaction transaction = transactionUseCase.deposit(mapper.toDepositDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(transaction));
    }

    @Operation(summary = "Process a withdrawal")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Withdrawal processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "422", description = "Insufficient balance or inactive account")
    })
    @PostMapping("/withdrawal")
    public ResponseEntity<TransactionResponse> withdrawal(
            @Valid @RequestBody WithdrawalRequest request) {
        Transaction transaction = transactionUseCase.withdrawal(mapper.toWithdrawalDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(transaction));
    }

    @Operation(summary = "Process a transfer between accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transfer processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "422", description = "Insufficient balance, inactive account or same account transfer")
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request) {
        Transaction transaction = transactionUseCase.transfer(mapper.toTransferDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(transaction));
    }

    @Operation(summary = "Get transactions by account id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of transactions"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> findByAccountId(
            @PathVariable Long accountId) {
        return ResponseEntity.ok(transactionUseCase.findByAccountId(accountId)
                .stream().map(mapper::toResponse).toList());
    }

}
