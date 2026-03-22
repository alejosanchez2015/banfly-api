package com.banfly.api.infrastructure.web.mapper;

import com.banfly.api.domain.product.model.Product;
import com.banfly.api.infrastructure.web.dto.request.ProductRequest;
import com.banfly.api.infrastructure.web.dto.response.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper {

    public Product toDomain(ProductRequest request) {
        return Product.builder()
                .accountType(request.getAccountType())
                .clientId(request.getClientId())
                .gmfExempt(request.isGmfExempt())
                .balance(request.getBalance())
                .availableBalance(request.getBalance())
                .build();
    }

    public ProductResponse toResponse(Product domain) {
        return ProductResponse.builder()
                .id(domain.getId())
                .accountType(domain.getAccountType())
                .accountNumber(domain.getAccountNumber())
                .status(domain.getStatus())
                .balance(domain.getBalance())
                .availableBalance(domain.getAvailableBalance())
                .gmfExempt(domain.isGmfExempt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .clientId(domain.getClientId())
                .build();
    }

}
