package com.banfly.api.application.service;

import com.banfly.api.domain.product.core.out.ProductRepository;
import com.banfly.api.domain.product.exception.InactiveAccountException;
import com.banfly.api.domain.product.exception.ProductNotFoundException;
import com.banfly.api.domain.product.model.AccountStatus;
import com.banfly.api.domain.product.model.AccountType;
import com.banfly.api.domain.product.model.Product;
import com.banfly.api.domain.transaction.core.out.TransactionRepository;
import com.banfly.api.domain.transaction.exception.InsufficientBalanceException;
import com.banfly.api.domain.transaction.exception.SameAccountTransferException;
import com.banfly.api.domain.transaction.model.Transaction;
import com.banfly.api.domain.transaction.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Product savingsAccount;
    private Product checkingAccount;
    private Transaction depositTransaction;
    private Transaction withdrawalTransaction;
    private Transaction transferTransaction;

    @BeforeEach
    void setUp() {
        savingsAccount = Product.builder()
                .id(1L)
                .accountType(AccountType.AHORROS)
                .accountNumber("5312345678")
                .status(AccountStatus.ACTIVA)
                .balance(new BigDecimal("100000.00"))
                .availableBalance(new BigDecimal("100000.00"))
                .clientId(1L)
                .build();

        checkingAccount = Product.builder()
                .id(2L)
                .accountType(AccountType.CORRIENTE)
                .accountNumber("3312345678")
                .status(AccountStatus.ACTIVA)
                .balance(new BigDecimal("200000.00"))
                .availableBalance(new BigDecimal("200000.00"))
                .clientId(1L)
                .build();

        depositTransaction = Transaction.builder()
                .targetAccountId(1L)
                .amount(new BigDecimal("50000.00"))
                .description("Salary deposit")
                .build();

        withdrawalTransaction = Transaction.builder()
                .sourceAccountId(1L)
                .amount(new BigDecimal("30000.00"))
                .description("ATM withdrawal")
                .build();

        transferTransaction = Transaction.builder()
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .amount(new BigDecimal("20000.00"))
                .description("Lunch payment")
                .build();
    }

    @Test
    void shouldProcessDepositSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));
        when(productRepository.save(any(Product.class))).thenReturn(savingsAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(depositTransaction);

        Transaction result = transactionService.deposit(depositTransaction);

        assertThat(result).isNotNull();
        verify(productRepository).save(any(Product.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldUpdateBalanceAfterDeposit() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(depositTransaction);

        transactionService.deposit(depositTransaction);

        assertThat(savingsAccount.getBalance())
                .isEqualByComparingTo(new BigDecimal("150000.00"));
        assertThat(savingsAccount.getAvailableBalance())
                .isEqualByComparingTo(new BigDecimal("150000.00"));
    }

    @Test
    void shouldThrowExceptionWhenDepositOnInactiveAccount() {
        savingsAccount.setStatus(AccountStatus.INACTIVA);
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));

        assertThatThrownBy(() -> transactionService.deposit(depositTransaction))
                .isInstanceOf(InactiveAccountException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDepositAccountNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.deposit(depositTransaction))
                .isInstanceOf(ProductNotFoundException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldProcessWithdrawalSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));
        when(productRepository.save(any(Product.class))).thenReturn(savingsAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(withdrawalTransaction);

        Transaction result = transactionService.withdrawal(withdrawalTransaction);

        assertThat(result).isNotNull();
        verify(productRepository).save(any(Product.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldUpdateBalanceAfterWithdrawal() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(withdrawalTransaction);

        transactionService.withdrawal(withdrawalTransaction);

        assertThat(savingsAccount.getBalance())
                .isEqualByComparingTo(new BigDecimal("70000.00"));
        assertThat(savingsAccount.getAvailableBalance())
                .isEqualByComparingTo(new BigDecimal("70000.00"));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientBalance() {
        withdrawalTransaction.setAmount(new BigDecimal("200000.00"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));

        assertThatThrownBy(() -> transactionService.withdrawal(withdrawalTransaction))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("5312345678");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenWithdrawalLeavesNegativeBalanceOnSavings() {
        savingsAccount.setBalance(new BigDecimal("10000.00"));
        savingsAccount.setAvailableBalance(new BigDecimal("10000.00"));
        withdrawalTransaction.setAmount(new BigDecimal("10000.01"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));

        assertThatThrownBy(() -> transactionService.withdrawal(withdrawalTransaction))
                .isInstanceOf(InsufficientBalanceException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenWithdrawalOnInactiveAccount() {
        savingsAccount.setStatus(AccountStatus.INACTIVA);
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));

        assertThatThrownBy(() -> transactionService.withdrawal(withdrawalTransaction))
                .isInstanceOf(InactiveAccountException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldProcessTransferSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));
        when(productRepository.findById(2L)).thenReturn(Optional.of(checkingAccount));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transferTransaction);

        Transaction result = transactionService.transfer(transferTransaction);

        assertThat(result).isNotNull();
        verify(productRepository, times(2)).save(any(Product.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldUpdateBothAccountBalancesAfterTransfer() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));
        when(productRepository.findById(2L)).thenReturn(Optional.of(checkingAccount));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transferTransaction);

        transactionService.transfer(transferTransaction);

        assertThat(savingsAccount.getBalance())
                .isEqualByComparingTo(new BigDecimal("80000.00"));
        assertThat(checkingAccount.getBalance())
                .isEqualByComparingTo(new BigDecimal("220000.00"));
    }

    @Test
    void shouldThrowExceptionWhenTransferToSameAccount() {
        transferTransaction.setTargetAccountId(1L);

        assertThatThrownBy(() -> transactionService.transfer(transferTransaction))
                .isInstanceOf(SameAccountTransferException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTransferWithInsufficientBalance() {
        transferTransaction.setAmount(new BigDecimal("500000.00"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));
        when(productRepository.findById(2L)).thenReturn(Optional.of(checkingAccount));

        assertThatThrownBy(() -> transactionService.transfer(transferTransaction))
                .isInstanceOf(InsufficientBalanceException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTransferSourceAccountIsInactive() {
        savingsAccount.setStatus(AccountStatus.INACTIVA);
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));

        assertThatThrownBy(() -> transactionService.transfer(transferTransaction))
                .isInstanceOf(InactiveAccountException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTransferTargetAccountIsInactive() {
        checkingAccount.setStatus(AccountStatus.INACTIVA);
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));
        when(productRepository.findById(2L)).thenReturn(Optional.of(checkingAccount));

        assertThatThrownBy(() -> transactionService.transfer(transferTransaction))
                .isInstanceOf(InactiveAccountException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldReturnAccountTransactionsSuccessfully() {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .transactionType(TransactionType.DEPOSIT)
                .amount(new BigDecimal("50000.00"))
                .createdAt(LocalDateTime.now())
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));
        when(transactionRepository.findByTargetAccountId(1L)).thenReturn(List.of(transaction));
        when(transactionRepository.findBySourceAccountId(1L)).thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.findByAccountId(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldReturnEmptyListWhenAccountHasNoTransactions() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(savingsAccount));
        when(transactionRepository.findByTargetAccountId(1L)).thenReturn(Collections.emptyList());
        when(transactionRepository.findBySourceAccountId(1L)).thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.findByAccountId(1L);

        assertThat(result).isEmpty();
    }
}
