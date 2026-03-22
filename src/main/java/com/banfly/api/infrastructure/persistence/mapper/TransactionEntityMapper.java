package com.banfly.api.infrastructure.persistence.mapper;

import com.banfly.api.domain.product.exception.ProductNotFoundException;
import com.banfly.api.domain.transaction.model.Transaction;
import com.banfly.api.infrastructure.persistence.entity.ProductEntity;
import com.banfly.api.infrastructure.persistence.entity.TransactionEntity;
import com.banfly.api.infrastructure.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEntityMapper {

    private final ProductJpaRepository productJpaRepository;

    public Transaction toDomain(TransactionEntity entity) {
        return Transaction.builder()
                .id(entity.getId())
                .transactionType(entity.getTransactionType())
                .amount(entity.getAmount())
                .createdAt(entity.getCreatedAt())
                .sourceAccountId(entity.getSourceAccount() != null
                        ? entity.getSourceAccount().getId() : null)
                .targetAccountId(entity.getTargetAccount() != null
                        ? entity.getTargetAccount().getId() : null)
                .description(entity.getDescription())
                .build();
    }

    public TransactionEntity toEntity(Transaction domain) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(domain.getId());
        entity.setTransactionType(domain.getTransactionType());
        entity.setAmount(domain.getAmount());
        entity.setDescription(domain.getDescription());

        if (domain.getSourceAccountId() != null) {
            ProductEntity source = productJpaRepository.findById(domain.getSourceAccountId())
                    .orElseThrow(() -> new ProductNotFoundException(domain.getSourceAccountId()));
            entity.setSourceAccount(source);
        }

        if (domain.getTargetAccountId() != null) {
            ProductEntity target = productJpaRepository.findById(domain.getTargetAccountId())
                    .orElseThrow(() -> new ProductNotFoundException(domain.getTargetAccountId()));
            entity.setTargetAccount(target);
        }

        return entity;
    }

}
