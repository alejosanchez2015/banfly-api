package com.banfly.api.application.service;

import com.banfly.api.domain.client.core.out.ClientRepository;
import com.banfly.api.domain.client.exception.ClientNotFoundException;
import com.banfly.api.domain.product.core.in.ProductUseCase;
import com.banfly.api.domain.product.core.out.ProductRepository;
import com.banfly.api.domain.product.exception.*;
import com.banfly.api.domain.product.model.AccountStatus;
import com.banfly.api.domain.product.model.AccountType;
import com.banfly.api.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {

    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    @Override
    public Product create(Product product) {
        validateClientExists(product.getClientId());
        validateInitialBalance(product);
        product.setAccountNumber(generateAccountNumber(product.getAccountType()));
        product.setStatus(AccountStatus.ACTIVA);
        return productRepository.save(product);
    }

    @Override
    public Product updateStatus(Long id, AccountStatus newStatus) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        validateStatusTransition(existing, newStatus);
        existing.setStatus(newStatus);
        return productRepository.save(existing);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public List<Product> findByClientId(Long clientId) {
        validateClientExists(clientId);
        return productRepository.findByClientId(clientId);
    }

    private void validateClientExists(Long clientId) {
        clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundForProductException(clientId));
    }

    private void validateStatusTransition(Product product, AccountStatus newStatus) {
        if (product.getStatus() == AccountStatus.CANCELADA) {
            throw new InvalidAccountStatusException(newStatus);
        }

        if (newStatus == AccountStatus.CANCELADA) {
            validateZeroBalanceForCancellation(product);
        }
    }

    private void validateZeroBalanceForCancellation(Product product) {
        if (product.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountCancellationException();
        }
    }

    private String generateAccountNumber(AccountType accountType) {
        String prefix = accountType == AccountType.AHORROS ? "53" : "33";
        String accountNumber;

        do {
            String suffix = String.format("%08d", (long) (Math.random() * 100_000_000));
            accountNumber = prefix + suffix;
        } while (productRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    private void validateInitialBalance(Product product) {
        if (product.getAccountType() == AccountType.AHORROS
                && product.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeBalanceException();
        }
    }

    public void validateAccountIsActive(Product product) {
        if (product.getStatus() != AccountStatus.ACTIVA) {
            throw new InactiveAccountException(product.getAccountNumber());
        }
    }

}
