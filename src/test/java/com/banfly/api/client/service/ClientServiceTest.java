package com.banfly.api.client.service;

import com.banfly.api.application.service.ClientService;
import com.banfly.api.domain.client.exception.ClientHasProductsException;
import com.banfly.api.domain.client.exception.ClientNotFoundException;
import com.banfly.api.domain.client.exception.DuplicateEmailException;
import com.banfly.api.domain.client.exception.UnderageClientException;
import com.banfly.api.domain.client.model.Client;
import com.banfly.api.domain.client.model.IdentificationType;
import com.banfly.api.domain.client.domain.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client client;

    @BeforeEach
    public void setUp() {
        client = Client.builder().
                id(1L).
                identificationType(IdentificationType.NIT).
                identificationNumber("123456789").
                firstName("Fly").
                lastName("Pass").
                email("flypasss@gmail.com").
                birthDate(LocalDate.of(1990,5,15)).
                createdAt(LocalDateTime.now()).
                updatedAt(LocalDateTime.now()).
                build();
    }


    @Test
    void shouldCreateClientSuccessfully() {
        when(clientRepository.findByEmail(client.getEmail()))
                .thenReturn(Optional.empty());
        when(clientRepository.save(client))
                .thenReturn(client);

        Client result = clientService.create(client);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(client.getEmail());
        verify(clientRepository).save(client);
    }

    @Test
    void shouldThrowExceptionWhenClientIsUnderage() {
        client.setBirthDate(LocalDate.now().minusYears(17));

        assertThatThrownBy(() -> clientService.create(client))
                .isInstanceOf(UnderageClientException.class)
                .hasMessageContaining("17");

        verify(clientRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(clientRepository.findByEmail(client.getEmail()))
                .thenReturn(Optional.of(client));

        assertThatThrownBy(() -> clientService.create(client))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining(client.getEmail());

        verify(clientRepository, never()).save(any());
    }

    @Test
    void shouldUpdateClientSuccessfully() {
        Client updatedData = Client.builder()
                .firstName("Pedro")
                .lastName("Perez")
                .email("pedro.pere@gmail.com")
                .birthDate(LocalDate.of(1992, 8, 20))
                .identificationType(IdentificationType.CC)
                .identificationNumber("1045673453")
                .build();

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class)))
                .thenReturn(client);

        Client result = clientService.update(1L, updatedData);

        assertThat(result).isNotNull();
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentClient() {
        when(clientRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.update(99L, client))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining("99");

        verify(clientRepository, never()).save(any());
    }


    @Test
    void shouldFindClientByIdSuccessfully() {
        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        Client result = clientService.findByID(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenClientNotFoundById() {
        when(clientRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findByID(99L))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining("99");
    }


    @Test
    void shouldFindClientByIdentificationNumberSuccessfully() {
        when(clientRepository.findByIdentificationNumber("123456789"))
                .thenReturn(Optional.of(client));

        Client result = clientService.findByIdentificationNumber("123456789");

        assertThat(result).isNotNull();
        assertThat(result.getIdentificationNumber()).isEqualTo("123456789");
    }

    @Test
    void shouldThrowExceptionWhenClientNotFoundByIdentificationNumber() {
        when(clientRepository.findByIdentificationNumber("0000000000"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findByIdentificationNumber("0000000000"))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining("0000000000");
    }

    @Test
    void shouldReturnAllClients() {
        when(clientRepository.findAll())
                .thenReturn(List.of(client));

        List<Client> result = clientService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldReturnEmptyListWhenNoClientsExist() {
        when(clientRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<Client> result = clientService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteClientSuccessfully() {
        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));
        when(clientRepository.hasProducts(1L))
                .thenReturn(false);

        assertThatCode(() -> clientService.delete(1L))
                .doesNotThrowAnyException();

        verify(clientRepository).delete(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentClient() {
        when(clientRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.delete(99L))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining("99");

        verify(clientRepository, never()).delete(any());
    }

    @Test
    void shouldThrowExceptionWhenDeletingClientWithProducts() {
        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));
        when(clientRepository.hasProducts(1L))
                .thenReturn(true);

        assertThatThrownBy(() -> clientService.delete(1L))
                .isInstanceOf(ClientHasProductsException.class)
                .hasMessageContaining("1");

        verify(clientRepository, never()).delete(any());
    }
}

