package com.banfly.api.application.infraestructure.web.controller;

import com.banfly.api.domain.product.exception.InactiveAccountException;
import com.banfly.api.domain.product.exception.ProductNotFoundException;
import com.banfly.api.domain.transaction.core.in.TransactionUseCase;
import com.banfly.api.domain.transaction.exception.InsufficientBalanceException;
import com.banfly.api.domain.transaction.exception.SameAccountTransferException;
import com.banfly.api.domain.transaction.model.Transaction;
import com.banfly.api.domain.transaction.model.TransactionType;
import com.banfly.api.infrastructure.web.controller.TransactionController;
import com.banfly.api.infrastructure.web.dto.request.DepositRequest;
import com.banfly.api.infrastructure.web.dto.request.TransferRequest;
import com.banfly.api.infrastructure.web.dto.request.WithdrawalRequest;
import com.banfly.api.infrastructure.web.dto.response.TransactionResponse;
import com.banfly.api.infrastructure.web.mapper.TransactionDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionUseCase transactionUseCase;

    @MockitoBean
    private TransactionDtoMapper mapper;

    private TransactionResponse validResponse;
    private DepositRequest depositRequest;
    private WithdrawalRequest withdrawalRequest;
    private TransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        validResponse = TransactionResponse.builder()
                .id(1L)
                .transactionType(TransactionType.DEPOSIT)
                .amount(new BigDecimal("50000.00"))
                .targetAccountId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        depositRequest = new DepositRequest();
        depositRequest.setTargetAccountId(1L);
        depositRequest.setAmount(new BigDecimal("50000.00"));
        depositRequest.setDescription("Salary deposit");

        withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setSourceAccountId(1L);
        withdrawalRequest.setAmount(new BigDecimal("30000.00"));
        withdrawalRequest.setDescription("ATM withdrawal");

        transferRequest = new TransferRequest();
        transferRequest.setSourceAccountId(1L);
        transferRequest.setTargetAccountId(2L);
        transferRequest.setAmount(new BigDecimal("20000.00"));
        transferRequest.setDescription("Payment transfer");
    }

    @Test
    void shouldProcessDepositAndReturn201() throws Exception {
        when(mapper.toDepositDomain(any())).thenReturn(Transaction.builder().build());
        when(transactionUseCase.deposit(any())).thenReturn(Transaction.builder().build());
        when(mapper.toResponse(any())).thenReturn(validResponse);

        mockMvc.perform(post("/api/v1/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.transactionType").value("DEPOSIT"));
    }

    @Test
    void shouldReturn400WhenDepositHasInvalidFields() throws Exception {
        mockMvc.perform(post("/api/v1/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DepositRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn422WhenDepositOnInactiveAccount() throws Exception {
        when(mapper.toDepositDomain(any())).thenReturn(Transaction.builder().build());
        when(transactionUseCase.deposit(any()))
                .thenThrow(new InactiveAccountException("5312345678"));

        mockMvc.perform(post("/api/v1/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn404WhenDepositAccountNotFound() throws Exception {
        when(mapper.toDepositDomain(any())).thenReturn(Transaction.builder().build());
        when(transactionUseCase.deposit(any()))
                .thenThrow(new ProductNotFoundException(1L));

        mockMvc.perform(post("/api/v1/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldProcessWithdrawalAndReturn201() throws Exception {
        when(mapper.toWithdrawalDomain(any())).thenReturn(Transaction.builder().build());
        when(transactionUseCase.withdrawal(any())).thenReturn(Transaction.builder().build());
        when(mapper.toResponse(any())).thenReturn(validResponse);

        mockMvc.perform(post("/api/v1/transactions/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldReturn400WhenWithdrawalHasInvalidFields() throws Exception {
        mockMvc.perform(post("/api/v1/transactions/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new WithdrawalRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn422WhenWithdrawalWithInsufficientBalance() throws Exception {
        when(mapper.toWithdrawalDomain(any())).thenReturn(Transaction.builder().build());
        when(transactionUseCase.withdrawal(any()))
                .thenThrow(new InsufficientBalanceException("5312345678"));

        mockMvc.perform(post("/api/v1/transactions/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn422WhenWithdrawalOnInactiveAccount() throws Exception {
        when(mapper.toWithdrawalDomain(any())).thenReturn(Transaction.builder().build());
        when(transactionUseCase.withdrawal(any()))
                .thenThrow(new InactiveAccountException("5312345678"));

        mockMvc.perform(post("/api/v1/transactions/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldProcessTransferAndReturn201() throws Exception {
        when(mapper.toTransferDomain(any())).thenReturn(Transaction.builder().build());
        when(transactionUseCase.transfer(any())).thenReturn(Transaction.builder().build());
        when(mapper.toResponse(any())).thenReturn(validResponse);

        mockMvc.perform(post("/api/v1/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldReturn400WhenTransferHasInvalidFields() throws Exception {
        mockMvc.perform(post("/api/v1/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransferRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn422WhenTransferToSameAccount() throws Exception {
        when(mapper.toTransferDomain(any())).thenReturn(Transaction.builder().build());
        when(transactionUseCase.transfer(any()))
                .thenThrow(new SameAccountTransferException());

        mockMvc.perform(post("/api/v1/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn422WhenTransferWithInsufficientBalance() throws Exception {
        when(mapper.toTransferDomain(any())).thenReturn(Transaction.builder().build());
        when(transactionUseCase.transfer(any()))
                .thenThrow(new InsufficientBalanceException("5312345678"));

        mockMvc.perform(post("/api/v1/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturnAccountTransactionsAndReturn200() throws Exception {
        when(transactionUseCase.findByAccountId(1L))
                .thenReturn(List.of(Transaction.builder()
                        .id(1L)
                        .transactionType(TransactionType.DEPOSIT)
                        .amount(new BigDecimal("50000.00"))
                        .createdAt(LocalDateTime.now())
                        .build()));
        when(mapper.toResponse(any())).thenReturn(validResponse);

        mockMvc.perform(get("/api/v1/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldReturnEmptyListAndReturn200WhenNoTransactions() throws Exception {
        when(transactionUseCase.findByAccountId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturn404WhenAccountNotFound() throws Exception {
        when(transactionUseCase.findByAccountId(99L))
                .thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/api/v1/transactions/account/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

}
