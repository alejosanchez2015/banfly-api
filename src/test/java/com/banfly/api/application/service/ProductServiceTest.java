package com.banfly.api.application.service;

import com.banfly.api.domain.client.core.out.ClientRepository;
import com.banfly.api.domain.client.model.Client;
import com.banfly.api.domain.product.core.out.ProductRepository;
import com.banfly.api.domain.product.exception.*;
import com.banfly.api.domain.product.model.AccountStatus;
import com.banfly.api.domain.product.model.AccountType;
import com.banfly.api.domain.product.model.Product;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ProductService productService;

    private Product validSavingsAccountProduct;
    private Product validCheckingAccountProduct;
    private Client validClient;

    @BeforeEach
    void setUp() {
        validClient = Client.builder()
                .id(1L)
                .firstName("Andres")
                .lastName("Montoya")
                .email("andresm@gmail.com")
                .build();

        validSavingsAccountProduct = Product.builder()
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

        validCheckingAccountProduct = Product.builder()
                .id(2L)
                .accountType(AccountType.CORRIENTE)
                .accountNumber("3312345678")
                .status(AccountStatus.ACTIVA)
                .balance(new BigDecimal("100000.00"))
                .availableBalance(new BigDecimal("100000.00"))
                .gmfExempt(false)
                .clientId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateSavingsAccountSuccessfully() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));
        when(productRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(validSavingsAccountProduct);

        Product result = productService.create(validSavingsAccountProduct);

        assertThat(result).isNotNull();
        assertThat(result.getAccountType()).isEqualTo(AccountType.AHORROS);
        assertThat(result.getStatus()).isEqualTo(AccountStatus.ACTIVA);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldCreateCheckingAccountSuccessfully() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));
        when(productRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(validCheckingAccountProduct);

        Product result = productService.create(validCheckingAccountProduct);

        assertThat(result).isNotNull();
        assertThat(result.getAccountType()).isEqualTo(AccountType.CORRIENTE);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenClientNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());
        validSavingsAccountProduct.setClientId(99L);

        assertThatThrownBy(() -> productService.create(validSavingsAccountProduct))
                .isInstanceOf(ClientNotFoundForProductException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSavingsAccountHasNegativeBalance() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));
        validSavingsAccountProduct.setBalance(new BigDecimal("-1000.00"));

        assertThatThrownBy(() -> productService.create(validSavingsAccountProduct))
                .isInstanceOf(NegativeBalanceException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldGenerateSavingsAccountNumberWithPrefix53() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));
        when(productRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.create(validSavingsAccountProduct);

        assertThat(result.getAccountNumber()).startsWith("53");
        assertThat(result.getAccountNumber()).hasSize(10);
    }

    @Test
    void shouldGenerateCheckingAccountNumberWithPrefix33() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));
        when(productRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.create(validCheckingAccountProduct);

        assertThat(result.getAccountNumber()).startsWith("33");
        assertThat(result.getAccountNumber()).hasSize(10);
    }

    @Test
    void shouldUpdateStatusToInactiveSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(validSavingsAccountProduct));
        when(productRepository.save(any(Product.class))).thenReturn(validSavingsAccountProduct);

        Product result = productService.updateStatus(1L, AccountStatus.INACTIVA);

        assertThat(result).isNotNull();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldUpdateStatusToActiveSuccessfully() {
        validSavingsAccountProduct.setStatus(AccountStatus.INACTIVA);
        when(productRepository.findById(1L)).thenReturn(Optional.of(validSavingsAccountProduct));
        when(productRepository.save(any(Product.class))).thenReturn(validSavingsAccountProduct);

        Product result = productService.updateStatus(1L, AccountStatus.ACTIVA);

        assertThat(result).isNotNull();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldCancelAccountWhenBalanceIsZero() {
        validSavingsAccountProduct.setBalance(BigDecimal.ZERO);
        when(productRepository.findById(1L)).thenReturn(Optional.of(validSavingsAccountProduct));
        when(productRepository.save(any(Product.class))).thenReturn(validSavingsAccountProduct);

        Product result = productService.updateStatus(1L, AccountStatus.CANCELADA);

        assertThat(result).isNotNull();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenCancellingAccountWithBalance() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(validSavingsAccountProduct));

        assertThatThrownBy(() -> productService.updateStatus(1L, AccountStatus.CANCELADA))
                .isInstanceOf(AccountCancellationException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingCancelledAccount() {
        validSavingsAccountProduct.setStatus(AccountStatus.CANCELADA);
        when(productRepository.findById(1L)).thenReturn(Optional.of(validSavingsAccountProduct));

        assertThatThrownBy(() -> productService.updateStatus(1L, AccountStatus.ACTIVA))
                .isInstanceOf(InvalidAccountStatusException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundOnUpdateStatus() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateStatus(99L, AccountStatus.INACTIVA))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void shouldFindProductByIdSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(validSavingsAccountProduct));

        Product result = productService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundById() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void shouldFindProductsByClientIdSuccessfully() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));
        when(productRepository.findByClientId(1L))
                .thenReturn(List.of(validSavingsAccountProduct, validCheckingAccountProduct));

        List<Product> result = productService.findByClientId(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnEmptyListWhenClientHasNoProducts() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));
        when(productRepository.findByClientId(1L)).thenReturn(Collections.emptyList());

        List<Product> result = productService.findByClientId(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenClientNotFoundOnFindByClientId() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findByClientId(99L))
                .isInstanceOf(ClientNotFoundForProductException.class)
                .hasMessageContaining("99");
    }

}
