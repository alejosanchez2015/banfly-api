package com.banfly.api.client.application.service;

import com.banfly.api.client.domain.core.ClientUseCase;
import com.banfly.api.client.domain.exception.ClientHasProductsException;
import com.banfly.api.client.domain.exception.ClientNotFoundException;
import com.banfly.api.client.domain.exception.DuplicateEmailException;
import com.banfly.api.client.domain.exception.UnderageClientException;
import com.banfly.api.client.domain.model.Client;
import com.banfly.api.client.domain.repository.ClientRepository;
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
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        existingClient.setFirstName(updatedClient.getFirstName());
        existingClient.setLastName(updatedClient.getLastName());
        existingClient.setEmail(updatedClient.getEmail());
        existingClient.setIdentificationType(updatedClient.getIdentificationType());
        existingClient.setIdentificationNumber(updatedClient.getIdentificationNumber());
        existingClient.setBirthDate(updatedClient.getBirthDate());

        return clientRepository.save(existingClient);
    }

    @Override
    public Client findByID(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    @Override
    public Client findByIdentificationNumber(String identificationNumber) {
        return clientRepository.findByIdentificationNumber(identificationNumber)
                .orElseThrow(() -> new ClientNotFoundException(identificationNumber));
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
            throw new DuplicateEmailException(email);
        });
    }
}
