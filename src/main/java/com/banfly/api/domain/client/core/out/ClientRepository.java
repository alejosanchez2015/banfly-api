package com.banfly.api.domain.client.core.out;

import com.banfly.api.domain.client.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findById(Long id);
    Optional<Client> findByIdentificationNumber(String identificationNumber);
    Optional<Client> findByEmail(String email);
    List<Client> findAll();
    boolean hasProducts(Long clientId);
    void delete(Long id);

}
