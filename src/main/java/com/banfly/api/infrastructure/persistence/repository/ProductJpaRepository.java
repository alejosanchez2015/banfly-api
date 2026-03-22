package com.banfly.api.infrastructure.persistence.repository;

import com.banfly.api.domain.product.model.Product;
import com.banfly.api.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByAccountNumber(String accountNumber);
    List<ProductEntity> findByClientId(Long clientId);
    boolean existsByAccountNumber(String accountNumber);

}
