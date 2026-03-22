package com.banfly.api.domain.transaction.core.in;

import com.banfly.api.domain.transaction.model.Transaction;

import java.util.List;

public interface TransactionUseCase {

    Transaction deposit(Transaction transaction);
    Transaction withdrawal(Transaction transaction);
    Transaction transfer(Transaction transaction);
    List<Transaction> findByAccountId(Long accountId);

}
