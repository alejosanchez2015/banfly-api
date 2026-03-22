package com.banfly.api.application.service;

import com.banfly.api.domain.client.core.in.ClientUseCase;
import com.banfly.api.domain.client.exception.ClientHasProductsException;
import com.banfly.api.domain.client.exception.ClientNotFoundException;
import com.banfly.api.domain.client.exception.DuplicateEmailException;
import com.banfly.api.domain.client.exception.UnderageClientException;
import com.banfly.api.domain.client.model.Client;
import com.banfly.api.domain.client.core.out.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService implements ClientUseCase {

    private final ClientRepository clientRepository;

    @Override
    public Client create(Client client){
        log.info("Creating a new client with email {}", client.getEmail());
        validateOverAge(client.getBirthDate());
        validateUniqueEmail(client.getEmail());
        Client savedClient = clientRepository.save(client);
        log.info("Client created successfully with id: {}", savedClient.getId());
        return savedClient;
    }

    @Override
    public Client update(Long id, Client updatedClient) {
        log.info("Updating client with id {}", updatedClient.getId());
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        existingClient.setFirstName(updatedClient.getFirstName());
        existingClient.setLastName(updatedClient.getLastName());
        existingClient.setEmail(updatedClient.getEmail());
        existingClient.setIdentificationType(updatedClient.getIdentificationType());
        existingClient.setIdentificationNumber(updatedClient.getIdentificationNumber());
        existingClient.setBirthDate(updatedClient.getBirthDate());
        Client updateClient = clientRepository.save(existingClient);
        log.info("Client updated successfully with id: {}", id);
        return updateClient;
    }

    @Override
    public Client findByID(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Client not found with id {}", id);
                    return new ClientNotFoundException(id);
                });
    }

    @Override
    public Client findByIdentificationNumber(String identificationNumber) {
        return clientRepository.findByIdentificationNumber(identificationNumber)
                .orElseThrow(() -> {
                    log.error("Client not found with identification number: {}", identificationNumber);
                    return new ClientNotFoundException(identificationNumber);
                });
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting client with id: {}", id);
        clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Client not found with id: {}", id);
                    return new ClientNotFoundException(id);
                });

        if (clientRepository.hasProducts(id)) {
            log.warn("CCan't delete client with id: {} — has linked products", id);
            throw new ClientHasProductsException(id);
        }

        clientRepository.delete(id);
        log.info("Client deleted successfully with id: {}", id);

    }

    private void validateOverAge(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18) {
            log.warn("Client creation rejected. Client underage, age: {}", age);
            throw new UnderageClientException(age);
        }
    }

    private void validateUniqueEmail(String email) {
        clientRepository.findByEmail(email).ifPresent(c -> {
            log.warn("Client creation rejected. Duplicate email");
            throw new DuplicateEmailException(email);
        });
    }
}
