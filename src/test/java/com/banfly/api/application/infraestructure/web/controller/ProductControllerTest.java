package com.banfly.api.application.infraestructure.web.controller;

import com.banfly.api.domain.product.core.in.ProductUseCase;
import com.banfly.api.domain.product.exception.AccountCancellationException;
import com.banfly.api.domain.product.exception.ClientNotFoundForProductException;
import com.banfly.api.domain.product.exception.NegativeBalanceException;
import com.banfly.api.domain.product.exception.ProductNotFoundException;
import com.banfly.api.domain.product.model.AccountStatus;
import com.banfly.api.domain.product.model.AccountType;
import com.banfly.api.domain.product.model.Product;
import com.banfly.api.infrastructure.web.controller.ProductController;
import com.banfly.api.infrastructure.web.dto.request.ProductRequest;
import com.banfly.api.infrastructure.web.dto.request.UpdateProductStatusRequest;
import com.banfly.api.infrastructure.web.dto.response.ProductResponse;
import com.banfly.api.infrastructure.web.mapper.ProductDtoMapper;
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


@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductUseCase productUseCase;

    @MockitoBean
    private ProductDtoMapper mapper;

    private Product validProduct;
    private ProductRequest validRequest;
    private ProductResponse validResponse;

    @BeforeEach
    void setUp() {
        validProduct = Product.builder()
                .id(1L)
                .accountType(AccountType.AHORROS)
                .accountNumber("5312345678")
                .status(AccountStatus.ACTIVA)
                .balance(new BigDecimal("50000.00"))
                .availableBalance(new BigDecimal("50000.00"))
                .gmfExempt(false)
                .clientId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        validRequest = new ProductRequest();
        validRequest.setAccountType(AccountType.AHORROS);
        validRequest.setClientId(1L);
        validRequest.setGmfExempt(false);
        validRequest.setBalance(new BigDecimal("50000.00"));

        validResponse = ProductResponse.builder()
                .id(1L)
                .accountType(AccountType.AHORROS)
                .accountNumber("5312345678")
                .status(AccountStatus.ACTIVA)
                .balance(new BigDecimal("50000.00"))
                .availableBalance(new BigDecimal("50000.00"))
                .gmfExempt(false)
                .clientId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateProductAndReturn200() throws Exception {
        when(mapper.toDomain(any(ProductRequest.class))).thenReturn(validProduct);
        when(productUseCase.create(any(Product.class))).thenReturn(validProduct);
        when(mapper.toResponse(any(Product.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("5312345678"))
                .andExpect(jsonPath("$.status").value("ACTIVA"));
    }

    @Test
    void shouldReturnErrorWhenRequestHasInvalidFields() throws Exception {
        ProductRequest invalidRequest = new ProductRequest();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnErrorWhenClientNotFound() throws Exception {
        when(mapper.toDomain(any(ProductRequest.class))).thenReturn(validProduct);
        when(productUseCase.create(any(Product.class)))
                .thenThrow(new ClientNotFoundForProductException(99L));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturnErrorWhenSavingsAccountHasNegativeBalance() throws Exception {
        when(mapper.toDomain(any(ProductRequest.class))).thenReturn(validProduct);
        when(productUseCase.create(any(Product.class)))
                .thenThrow(new NegativeBalanceException());

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldUpdateStatusAndReturn200() throws Exception {
        UpdateProductStatusRequest statusRequest = new UpdateProductStatusRequest();
        statusRequest.setStatus(AccountStatus.INACTIVA);

        when(productUseCase.updateStatus(eq(1L), any(AccountStatus.class)))
                .thenReturn(validProduct);
        when(mapper.toResponse(any(Product.class))).thenReturn(validResponse);

        mockMvc.perform(patch("/api/v1/products/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldReturnErrorWhenCancellingAccountWithBalance() throws Exception {
        UpdateProductStatusRequest statusRequest = new UpdateProductStatusRequest();
        statusRequest.setStatus(AccountStatus.CANCELADA);

        when(productUseCase.updateStatus(eq(1L), any(AccountStatus.class)))
                .thenThrow(new AccountCancellationException());

        mockMvc.perform(patch("/api/v1/products/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturnErrorWhenProductNotFound() throws Exception {
        UpdateProductStatusRequest statusRequest = new UpdateProductStatusRequest();
        statusRequest.setStatus(AccountStatus.INACTIVA);

        when(productUseCase.updateStatus(eq(99L), any(AccountStatus.class)))
                .thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(patch("/api/v1/products/99/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldFindProductByIdAndReturn200() throws Exception {
        when(productUseCase.findById(1L)).thenReturn(validProduct);
        when(mapper.toResponse(any(Product.class))).thenReturn(validResponse);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("5312345678"));
    }

    @Test
    void shouldReturnErrorWhenProductNotFoundById() throws Exception {
        when(productUseCase.findById(99L))
                .thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldFindProductsByClientIdAndReturn200() throws Exception {
        when(productUseCase.findByClientId(1L)).thenReturn(List.of(validProduct));
        when(mapper.toResponse(any(Product.class))).thenReturn(validResponse);

        mockMvc.perform(get("/api/v1/products/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldReturnEmptyListWhenClientHasNoProducts() throws Exception {
        when(productUseCase.findByClientId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/products/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnErrorWhenClientNotFoundOnFindByClientId() throws Exception {
        when(productUseCase.findByClientId(99L))
                .thenThrow(new ClientNotFoundForProductException(99L));

        mockMvc.perform(get("/api/v1/products/client/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}

