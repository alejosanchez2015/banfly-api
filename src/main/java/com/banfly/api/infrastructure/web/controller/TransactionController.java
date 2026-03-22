package com.banfly.api.infrastructure.web.controller;

import com.banfly.api.domain.transaction.core.in.TransactionUseCase;
import com.banfly.api.domain.transaction.model.Transaction;
import com.banfly.api.infrastructure.web.dto.request.DepositRequest;
import com.banfly.api.infrastructure.web.dto.request.TransferRequest;
import com.banfly.api.infrastructure.web.dto.request.WithdrawalRequest;
import com.banfly.api.infrastructure.web.dto.response.TransactionResponse;
import com.banfly.api.infrastructure.web.mapper.TransactionDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionUseCase transactionUseCase;
    private final TransactionDtoMapper mapper;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody DepositRequest request) {
        Transaction transaction = transactionUseCase.deposit(mapper.toDepositDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(transaction));
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<TransactionResponse> withdrawal(
            @Valid @RequestBody WithdrawalRequest request) {
        Transaction transaction = transactionUseCase.withdrawal(mapper.toWithdrawalDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(transaction));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request) {
        Transaction transaction = transactionUseCase.transfer(mapper.toTransferDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(transaction));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> findByAccountId(
            @PathVariable Long accountId) {
        return ResponseEntity.ok(transactionUseCase.findByAccountId(accountId)
                .stream().map(mapper::toResponse).toList());
    }

}
