package com.banfly.api.infrastructure.web.controller;

import com.banfly.api.domain.product.core.in.ProductUseCase;
import com.banfly.api.domain.product.model.Product;
import com.banfly.api.infrastructure.web.dto.request.ProductRequest;
import com.banfly.api.infrastructure.web.dto.request.UpdateProductStatusRequest;
import com.banfly.api.infrastructure.web.dto.response.ProductResponse;
import com.banfly.api.infrastructure.web.mapper.ProductDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product (accounts) management endpoints")
public class ProductController {

    private final ProductUseCase productUseCase;
    private final ProductDtoMapper mapper;

    @Operation(summary = "Create a new product")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "422", description = "Negative balance for savings account")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        Product product = productUseCase.create(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(product));
    }

    @Operation(summary = "Update product status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "422", description = "Invalid status transition")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductStatusRequest request) {
        Product product = productUseCase.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(mapper.toResponse(product));
    }

    @Operation(summary = "Find product by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(productUseCase.findById(id)));
    }

    @Operation(summary = "Find products by client id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of products"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ProductResponse>> findByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(productUseCase.findByClientId(clientId).stream()
                .map(mapper::toResponse).toList());
    }

}
