package com.banfly.api.infrastructure.persistence.mapper;

import com.banfly.api.domain.client.model.Client;
import com.banfly.api.infrastructure.persistence.entity.ClientEntity;
import org.springframework.stereotype.Component;

@Component
public class ClientEntityMapper {

    public Client toDomain(ClientEntity entity) {
        return Client.builder()
                .id(entity.getId())
                .identificationType(entity.getIdentificationType())
                .identificationNumber(entity.getIdentificationNumber())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .birthDate(entity.getBirthDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ClientEntity toEntity(Client domain) {
        ClientEntity entity = new ClientEntity();
        entity.setId(domain.getId());
        entity.setIdentificationType(domain.getIdentificationType());
        entity.setIdentificationNumber(domain.getIdentificationNumber());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setBirthDate(domain.getBirthDate());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

}
