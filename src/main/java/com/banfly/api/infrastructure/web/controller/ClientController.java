package com.banfly.api.infrastructure.web.controller;

import com.banfly.api.domain.client.core.in.ClientUseCase;
import com.banfly.api.domain.client.model.Client;
import com.banfly.api.infrastructure.web.dto.request.ClientRequest;
import com.banfly.api.infrastructure.web.dto.response.ClientResponse;
import com.banfly.api.infrastructure.web.mapper.ClientDtoMapper;
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
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Client management endpoints")
public class ClientController {

    private final ClientUseCase clientUseCase;
    private final ClientDtoMapper mapper;

    @Operation(summary = "Create a new client")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Email already exists"),
            @ApiResponse(responseCode = "422", description = "Underage client")
    })
    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientRequest request) {
        Client client = clientUseCase.create(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(client));
    }

    @Operation(summary = "Update an existing client")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client updated successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {
        Client client = clientUseCase.update(id, mapper.toDomain(request));
        return ResponseEntity.ok(mapper.toResponse(client));
    }

    @Operation(summary = "Find client by identification number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client found"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/{identificationNumber}")
    public ResponseEntity<ClientResponse> findByIdentificationNumber(@PathVariable String identificationNumber) {
        return ResponseEntity.ok(mapper.toResponse(clientUseCase.findByIdentificationNumber(identificationNumber)));
    }

    @Operation(summary = "Get all clients")
    @ApiResponse(responseCode = "200", description = "List of clients")
    @GetMapping
    public ResponseEntity<List<ClientResponse>> findAll() {
        return ResponseEntity.ok(clientUseCase.findAll().stream()
                .map(mapper::toResponse).toList());
    }

    @Operation(summary = "Delete a client")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Client deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "409", description = "Client has linked products")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

}
