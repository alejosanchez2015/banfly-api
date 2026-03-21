package com.banfly.api.client.infraestructure.persistence.repository;

import com.banfly.api.client.domain.model.Client;
import com.banfly.api.client.domain.repository.ClientRepository;
import com.banfly.api.client.infraestructure.persistence.mapper.ClientEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {

    private final ClientJpaRepository jpaRepository;
    private final ClientEntityMapper mapper;

    @Override
    public Client save(Client client) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(client)));
    }

    @Override
    public Optional<Client> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public List<Client> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Client> findByIdentificationNumber(String identificationNumber) {
        return jpaRepository.findByIdentificationNumber(identificationNumber).map(mapper::toDomain);
    }

    @Override
    public boolean hasProducts(Long clientId) {

        // return jpaRepository.hasProducts(clientId);
        return false;
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }
}
