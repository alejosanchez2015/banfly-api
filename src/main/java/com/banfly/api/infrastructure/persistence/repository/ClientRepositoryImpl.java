package com.banfly.api.infrastructure.persistence.repository;

import com.banfly.api.domain.client.model.Client;
import com.banfly.api.domain.client.core.out.ClientRepository;
import com.banfly.api.infrastructure.persistence.mapper.ClientEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {

    private final ClientJpaRepository ClientJpaRepository;
    private final ClientEntityMapper mapper;

    @Override
    public Client save(Client client) {
        return mapper.toDomain(ClientJpaRepository.save(mapper.toEntity(client)));
    }

    @Override
    public Optional<Client> findById(Long id) {
        return ClientJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        return ClientJpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public List<Client> findAll() {
        return ClientJpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Client> findByIdentificationNumber(String identificationNumber) {
        return ClientJpaRepository.findByIdentificationNumber(identificationNumber).map(mapper::toDomain);
    }

    @Override
    public boolean hasProducts(Long clientId) {
        return ClientJpaRepository.hasProducts(clientId);
    }

    @Override
    public void delete(Long id) {
        ClientJpaRepository.deleteById(id);
    }
}
