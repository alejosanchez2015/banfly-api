package com.banfly.api.domain.transaction.core.out;

import com.banfly.api.domain.transaction.model.Transaction;

import java.util.List;

public interface TransactionRepository {

    Transaction save(Transaction transaction);
    List<Transaction> findBySourceAccountId(Long accountId);
    List<Transaction> findByTargetAccountId(Long accountId);

}
