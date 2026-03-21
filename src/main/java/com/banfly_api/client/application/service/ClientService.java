package com.banfly_api.client.application.service;

import com.banfly_api.client.domain.core.ClientUseCase;
import com.banfly_api.client.domain.exception.ClientHasProductsException;
import com.banfly_api.client.domain.exception.ClientNotFoundException;
import com.banfly_api.client.domain.exception.UnderageClientException;
import com.banfly_api.client.domain.model.Client;
import com.banfly_api.client.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService implements ClientUseCase {

    private final ClientRepository clientRepository;

    @Override
    public Client create(Client client){
        validateOverAge(client.getBirthDate());
        validateUniqueEmail(client.getEmail());

        return clientRepository.save(client);
    }

    @Override
    public Client update(Long id, Client updatedClient) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        client.setFirstName(updatedClient.getFirstName());
        client.setFirstName(updatedClient.getLastName());
        client.setEmail(updatedClient.getEmail());
        client.setIdentificationType(updatedClient.getIdentificationType());
        client.setIdentificationNumber(updatedClient.getIdentificationNumber());
        client.setBirthDate(updatedClient.getBirthDate());

        return clientRepository.save(client);
    }

    @Override
    public Client findByID(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        if (clientRepository.hasProducts(id)) {
            throw new ClientHasProductsException(id);
        }

        clientRepository.delete(id);
    }

    private void validateOverAge(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18) {
            throw new UnderageClientException(age);
        }
    }

    private void validateUniqueEmail(String email) {
        clientRepository.findByEmail(email).ifPresent(c -> {
            throw new IllegalArgumentException(
                    "There is already a registered customer with the email address: " + email);
        });
    }
}
