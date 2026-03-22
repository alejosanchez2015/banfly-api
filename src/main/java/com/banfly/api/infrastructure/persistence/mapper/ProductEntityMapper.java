package com.banfly.api.infrastructure.persistence.mapper;

import com.banfly.api.domain.client.exception.ClientNotFoundException;
import com.banfly.api.domain.product.model.Product;
import com.banfly.api.infrastructure.persistence.entity.ClientEntity;
import com.banfly.api.infrastructure.persistence.entity.ProductEntity;
import com.banfly.api.infrastructure.persistence.repository.ClientJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityMapper {

    @Autowired
    private ClientJpaRepository clientJpaRepository;

    public Product toDomain(ProductEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .accountType(entity.getAccountType())
                .accountNumber(entity.getAccountNumber())
                .status(entity.getStatus())
                .balance(entity.getBalance())
                .availableBalance(entity.getAvailableBalance())
                .gmfExempt(entity.isGmfExempt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .clientId(entity.getClient().getId())
                .build();
    }

    public ProductEntity toEntity(Product domain) {
        ProductEntity entity = new ProductEntity();
        entity.setId(domain.getId());
        entity.setAccountType(domain.getAccountType());
        entity.setAccountNumber(domain.getAccountNumber());
        entity.setStatus(domain.getStatus());
        entity.setBalance(domain.getBalance());
        entity.setAvailableBalance(domain.getAvailableBalance());
        entity.setGmfExempt(domain.isGmfExempt());
        entity.setCreatedAt(domain.getCreatedAt());

        ClientEntity clientEntity = clientJpaRepository.findById(domain.getClientId())
                .orElseThrow(() -> new ClientNotFoundException(domain.getClientId()));
        entity.setClient(clientEntity);

        return entity;
    }

}
