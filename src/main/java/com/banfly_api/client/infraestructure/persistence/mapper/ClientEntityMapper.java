package com.banfly_api.client.infraestructure.persistence.mapper;

import com.banfly_api.client.domain.model.Client;
import com.banfly_api.client.infraestructure.persistence.entity.ClientEntity;
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
        return entity;
    }

}
