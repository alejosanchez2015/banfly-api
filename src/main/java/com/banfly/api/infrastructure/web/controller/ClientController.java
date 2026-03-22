package com.banfly.api.infrastructure.web.controller;

import com.banfly.api.domain.client.core.ClientUseCase;
import com.banfly.api.domain.client.model.Client;
import com.banfly.api.infrastructure.web.dto.request.ClientRequest;
import com.banfly.api.infrastructure.web.dto.response.ClientResponse;
import com.banfly.api.infrastructure.web.mapper.ClientDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientUseCase clientUseCase;
    private final ClientDtoMapper mapper;

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientRequest request) {
        Client client = clientUseCase.create(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(client));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {
        Client client = clientUseCase.update(id, mapper.toDomain(request));
        return ResponseEntity.ok(mapper.toResponse(client));
    }

    @GetMapping("/{identificationNumber}")
    public ResponseEntity<ClientResponse> findByIdentificationNumber(@PathVariable String identificationNumber) {
        return ResponseEntity.ok(mapper.toResponse(clientUseCase.findByIdentificationNumber(identificationNumber)));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> findAll() {
        return ResponseEntity.ok(clientUseCase.findAll().stream()
                .map(mapper::toResponse).toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

}
