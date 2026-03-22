package com.banfly.api.domain.client.core;

import com.banfly.api.domain.client.model.Client;

import java.util.List;

public interface ClientUseCase {
    Client create(Client client);
    Client update(Long id, Client client);
    Client findByID(Long id);
    Client findByIdentificationNumber(String identificationNumber);
    List<Client> findAll();
    void delete(Long id);

}
