package com.banfly.api.infrastructure.web.mapper;

import com.banfly.api.domain.client.model.Client;
import com.banfly.api.infrastructure.web.dto.request.ClientRequest;
import com.banfly.api.infrastructure.web.dto.response.ClientResponse;
import org.springframework.stereotype.Component;

@Component
public class ClientDtoMapper {

    public Client toDomain(ClientRequest request) {
        return Client.builder()
                .identificationType(request.getIdentificationType())
                .identificationNumber(request.getIdentificationNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .birthDate(request.getBirthDate())
                .build();
    }

    public ClientResponse toResponse(Client domain) {
        return ClientResponse.builder()
                .id(domain.getId())
                .identificationType(domain.getIdentificationType())
                .identificationNumber(domain.getIdentificationNumber())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .email(domain.getEmail())
                .birthDate(domain.getBirthDate())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

}
