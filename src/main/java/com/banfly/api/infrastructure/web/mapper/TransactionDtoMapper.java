package com.banfly.api.infrastructure.web.mapper;

import com.banfly.api.domain.transaction.model.Transaction;
import com.banfly.api.infrastructure.web.dto.request.DepositRequest;
import com.banfly.api.infrastructure.web.dto.request.TransferRequest;
import com.banfly.api.infrastructure.web.dto.request.WithdrawalRequest;
import com.banfly.api.infrastructure.web.dto.response.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionDtoMapper {

    public Transaction toDepositDomain(DepositRequest request) {
        return Transaction.builder()
                .targetAccountId(request.getTargetAccountId())
                .amount(request.getAmount())
                .description(request.getDescription())
                .build();
    }

    public Transaction toWithdrawalDomain(WithdrawalRequest request) {
        return Transaction.builder()
                .sourceAccountId(request.getSourceAccountId())
                .amount(request.getAmount())
                .description(request.getDescription())
                .build();
    }

    public Transaction toTransferDomain(TransferRequest request) {
        return Transaction.builder()
                .sourceAccountId(request.getSourceAccountId())
                .targetAccountId(request.getTargetAccountId())
                .amount(request.getAmount())
                .description(request.getDescription())
                .build();
    }

    public TransactionResponse toResponse(Transaction domain) {
        return TransactionResponse.builder()
                .id(domain.getId())
                .transactionType(domain.getTransactionType())
                .amount(domain.getAmount())
                .createdAt(domain.getCreatedAt())
                .sourceAccountId(domain.getSourceAccountId())
                .targetAccountId(domain.getTargetAccountId())
                .description(domain.getDescription())
                .build();
    }

}
