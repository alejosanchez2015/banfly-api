package com.banfly.api.infrastructure.persistence.repository;

import com.banfly.api.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findBySourceAccountId(Long sourceAccountId);
    List<TransactionEntity> findByTargetAccountId(Long targetAccountId);

}
