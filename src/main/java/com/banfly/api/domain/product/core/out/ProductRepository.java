package com.banfly.api.domain.product.core.out;

import com.banfly.api.domain.product.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);
    Optional<Product> findById(Long id);
    Optional<Product> findByAccountNumber(String accountNumber);
    List<Product> findByClientId(Long clientId);
    boolean existsByAccountNumber(String accountNumber);

}
