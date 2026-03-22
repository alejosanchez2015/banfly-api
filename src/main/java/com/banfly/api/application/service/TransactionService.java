package com.banfly.api.application.service;

import com.banfly.api.domain.product.core.out.ProductRepository;
import com.banfly.api.domain.product.exception.InactiveAccountException;
import com.banfly.api.domain.product.exception.NegativeBalanceException;
import com.banfly.api.domain.product.exception.ProductNotFoundException;
import com.banfly.api.domain.product.model.AccountStatus;
import com.banfly.api.domain.product.model.AccountType;
import com.banfly.api.domain.product.model.Product;
import com.banfly.api.domain.transaction.core.in.TransactionUseCase;
import com.banfly.api.domain.transaction.core.out.TransactionRepository;
import com.banfly.api.domain.transaction.exception.InsufficientBalanceException;
import com.banfly.api.domain.transaction.exception.SameAccountTransferException;
import com.banfly.api.domain.transaction.model.Transaction;
import com.banfly.api.domain.transaction.model.TransactionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService implements TransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Transaction deposit(Transaction transaction) {
        log.info("Processing deposit. TargetAccountId: {}, amount: {}",
                transaction.getTargetAccountId(), transaction.getAmount());

        Product targetAccount = findActiveProduct(transaction.getTargetAccountId());

        targetAccount.setBalance(targetAccount.getBalance().add(transaction.getAmount()));
        targetAccount.setAvailableBalance(targetAccount.getAvailableBalance().add(transaction.getAmount()));
        productRepository.save(targetAccount);

        transaction.setTransactionType(TransactionType.DEPOSIT);
        Transaction transactionSaved = transactionRepository.save(transaction);
        log.info("Deposit processed successfully. TransactionId: {}, newBalance: {}",
                transactionSaved.getId(), targetAccount.getBalance());
        return transactionSaved;

    }

    @Override
    @Transactional
    public Transaction withdrawal(Transaction transaction) {
        log.info("Processing withdrawal. SourceAccountId: {}, amount: {}",
                transaction.getSourceAccountId(), transaction.getAmount());

        Product sourceAccount = findActiveProduct(transaction.getSourceAccountId());

        validateSufficientBalance(sourceAccount, transaction.getAmount());
        validateSavingsNonNegativeBalance(sourceAccount, transaction.getAmount());

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(transaction.getAmount()));
        sourceAccount.setAvailableBalance(sourceAccount.getAvailableBalance().subtract(transaction.getAmount()));
        productRepository.save(sourceAccount);

        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        Transaction withdrawalSaved = transactionRepository.save(transaction);
        log.info("Withdrawal processed successfully. TransactionId: {}, newBalance: {}",
                withdrawalSaved.getId(), sourceAccount.getBalance());
        return withdrawalSaved;

    }

    @Override
    @Transactional
    public Transaction transfer(Transaction transaction) {
        log.info("Processing transfer. SourceAccountId: {}, targetAccountId: {}, amount: {}",
                transaction.getSourceAccountId(),
                transaction.getTargetAccountId(),
                transaction.getAmount());

        validateDifferentAccounts(transaction);

        Product sourceAccount = findActiveProduct(transaction.getSourceAccountId());
        Product targetAccount = findActiveProduct(transaction.getTargetAccountId());

        validateSufficientBalance(sourceAccount, transaction.getAmount());
        validateSavingsNonNegativeBalance(sourceAccount, transaction.getAmount());

        // Débito en cuenta origen
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(transaction.getAmount()));
        sourceAccount.setAvailableBalance(sourceAccount.getAvailableBalance().subtract(transaction.getAmount()));
        productRepository.save(sourceAccount);

        // Crédito en cuenta destino
        targetAccount.setBalance(targetAccount.getBalance().add(transaction.getAmount()));
        targetAccount.setAvailableBalance(targetAccount.getAvailableBalance().add(transaction.getAmount()));
        productRepository.save(targetAccount);

        transaction.setTransactionType(TransactionType.TRANSFER);
        Transaction transferSaved = transactionRepository.save(transaction);
        log.info("Transfer processed successfully. TransactionId: {}", transferSaved.getId());
        return transferSaved;

    }

    @Override
    public List<Transaction> findByAccountId(Long accountId) {
        log.info("Fetching transactions for accountId: {}", accountId);
        findActiveProduct(accountId);
        List<Transaction> deposits = transactionRepository.findByTargetAccountId(accountId);
        List<Transaction> withdrawals = transactionRepository.findBySourceAccountId(accountId);

        return Stream.concat(deposits.stream(), withdrawals.stream())
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .toList();
    }

    private Product findActiveProduct(Long accountId) {
        Product product = productRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found with id: {}", accountId);
                    return new ProductNotFoundException(accountId);
                });

        if (product.getStatus() != AccountStatus.ACTIVA) {
            log.warn("Transaction rejected. Account is not active, accountId: {}", accountId);
            throw new InactiveAccountException(product.getAccountNumber());
        }

        return product;
    }

    private void validateSufficientBalance(Product account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            log.warn("Transaction rejected. insufficient balance, accountId: {}", account.getId());
            throw new InsufficientBalanceException(account.getAccountNumber());
        }
    }

    private void validateSavingsNonNegativeBalance(Product account, BigDecimal amount) {
        if (account.getAccountType() == AccountType.AHORROS) {
            BigDecimal resultingBalance = account.getBalance().subtract(amount);
            if (resultingBalance.compareTo(BigDecimal.ZERO) < 0) {
                log.warn("Transaction rejected. Savings account would have negative balance, accountId: {}", account.getId());
                throw new NegativeBalanceException();
            }
        }
    }

    private void validateDifferentAccounts(Transaction transaction) {
        if (transaction.getSourceAccountId().equals(transaction.getTargetAccountId())) {
            log.warn("Transfer rejected. Source and target accounts are the same, accountId: {}", transaction.getSourceAccountId());
            throw new SameAccountTransferException();
        }
    }

}
