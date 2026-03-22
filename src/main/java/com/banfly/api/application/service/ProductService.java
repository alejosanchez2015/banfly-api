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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements ProductUseCase {

    private static final String PRODUCT_NOT_FOUND = "Product not found with id: {}";

    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    @Override
    public Product create(Product product) {
        log.info("Creating product. AccountType: {}, clientId: {}",
                product.getAccountType(), product.getClientId());

        validateClientExists(product.getClientId());
        validateInitialBalance(product);
        product.setAccountNumber(generateAccountNumber(product.getAccountType()));
        product.setStatus(AccountStatus.ACTIVA);
        Product productSaved = productRepository.save(product);
        log.info("Product created successfully. AccountNumber: {}", productSaved.getAccountNumber());
        return productSaved;

    }

    @Override
    public Product updateStatus(Long id, AccountStatus newStatus) {
        log.info("Updating product status. Id: {}, newStatus: {}", id, newStatus);
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(PRODUCT_NOT_FOUND, id);
                    return new ProductNotFoundException(id);
                });

        validateStatusTransition(existing, newStatus);
        existing.setStatus(newStatus);
        Product updatedProduct = productRepository.save(existing);
        log.info("Product status updated successfully — id: {}, status: {}", id, newStatus);
        return updatedProduct;

    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(PRODUCT_NOT_FOUND, id);
                    return new ProductNotFoundException(id);
                });

    }

    @Override
    public List<Product> findByClientId(Long clientId) {
        log.info("Fetching products for clientId: {}", clientId);
        validateClientExists(clientId);
        return productRepository.findByClientId(clientId);
    }

    private void validateClientExists(Long clientId) {
        clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("Client not found with id: {}", clientId);
                    return new ClientNotFoundForProductException(clientId);
                });

    }

    private void validateStatusTransition(Product product, AccountStatus newStatus) {
        if (product.getStatus() == AccountStatus.CANCELADA) {
            log.warn("Can't update status. Product is cancelled, id: {}", product.getId());
            throw new InvalidAccountStatusException(newStatus);
        }

        if (newStatus == AccountStatus.CANCELADA) {
            validateZeroBalanceForCancellation(product);
        }
    }

    private void validateZeroBalanceForCancellation(Product product) {
        if (product.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            log.warn("Can't cancel product. Balance is not zero, id: {}", product.getId());
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
