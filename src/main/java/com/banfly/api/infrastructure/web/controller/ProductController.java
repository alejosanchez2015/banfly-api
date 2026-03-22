package com.banfly.api.infrastructure.web.controller;

import com.banfly.api.domain.product.core.in.ProductUseCase;
import com.banfly.api.domain.product.model.Product;
import com.banfly.api.infrastructure.web.dto.request.ProductRequest;
import com.banfly.api.infrastructure.web.dto.request.UpdateProductStatusRequest;
import com.banfly.api.infrastructure.web.dto.response.ProductResponse;
import com.banfly.api.infrastructure.web.mapper.ProductDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductUseCase productUseCase;
    private final ProductDtoMapper mapper;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        Product product = productUseCase.create(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(product));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductStatusRequest request) {
        Product product = productUseCase.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(mapper.toResponse(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(productUseCase.findById(id)));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ProductResponse>> findByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(productUseCase.findByClientId(clientId).stream()
                .map(mapper::toResponse).toList());
    }

}
