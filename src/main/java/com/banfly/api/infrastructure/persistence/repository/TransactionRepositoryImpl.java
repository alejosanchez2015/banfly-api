package com.banfly.api.infrastructure.persistence.repository;

import com.banfly.api.domain.transaction.core.out.TransactionRepository;
import com.banfly.api.domain.transaction.model.Transaction;
import com.banfly.api.infrastructure.persistence.entity.TransactionEntity;
import com.banfly.api.infrastructure.persistence.mapper.TransactionEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionEntityMapper mapper;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = mapper.toEntity(transaction);
        TransactionEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Transaction> findBySourceAccountId(Long accountId) {
        return jpaRepository.findBySourceAccountId(accountId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Transaction> findByTargetAccountId(Long accountId) {
        return jpaRepository.findByTargetAccountId(accountId)
                .stream().map(mapper::toDomain).toList();
    }

}
