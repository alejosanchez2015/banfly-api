package com.banfly.api.domain.product.core.in;

import com.banfly.api.domain.product.model.AccountStatus;
import com.banfly.api.domain.product.model.Product;

import java.util.List;

public interface ProductUseCase {

    Product create(Product product);
    Product updateStatus(Long id, AccountStatus newStatus);
    Product findById(Long id);
    List<Product> findByClientId(Long clientId);

}
