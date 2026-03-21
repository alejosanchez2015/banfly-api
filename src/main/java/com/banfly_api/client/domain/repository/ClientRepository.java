package com.banfly_api.client.domain.repository;

import com.banfly_api.client.domain.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findById(Long id);
    Optional<Client> findByEmail(String email);
    List<Client> findAll();
    boolean hasProducts(Long clientId);
    void delete(Long id);

}
