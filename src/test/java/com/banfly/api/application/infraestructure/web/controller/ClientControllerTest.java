package com.banfly.api.application.infraestructure.web.controller;

import com.banfly.api.domain.client.core.in.ClientUseCase;
import com.banfly.api.domain.client.exception.ClientHasProductsException;
import com.banfly.api.domain.client.exception.ClientNotFoundException;
import com.banfly.api.domain.client.exception.DuplicateEmailException;
import com.banfly.api.domain.client.exception.UnderageClientException;
import com.banfly.api.domain.client.model.Client;
import com.banfly.api.domain.client.model.IdentificationType;
import com.banfly.api.infrastructure.web.controller.ClientController;
import com.banfly.api.infrastructure.web.dto.request.ClientRequest;
import com.banfly.api.infrastructure.web.dto.response.ClientResponse;
import com.banfly.api.infrastructure.web.mapper.ClientDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ClientController.class)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientUseCase clientUseCase;

    @MockitoBean
    private ClientDtoMapper mapper;

    private Client validClient;
    private ClientRequest validRequest;
    private ClientResponse validResponse;

    @BeforeEach
    void setUp() {
        validClient = Client.builder()
                .id(1L)
                .identificationType(IdentificationType.CC)
                .identificationNumber("987654321")
                .firstName("Jesus")
                .lastName("Marin")
                .email("jesumarin@gmail.com")
                .birthDate(LocalDate.of(1990, 5, 15))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        validRequest = new ClientRequest();
        validRequest.setIdentificationType(IdentificationType.CC);
        validRequest.setIdentificationNumber("987654321");
        validRequest.setFirstName("Jesus");
        validRequest.setLastName("Marin");
        validRequest.setEmail("jesumarin@gmail.com");
        validRequest.setBirthDate(LocalDate.of(1990, 5, 15));

        validResponse = ClientResponse.builder()
                .id(1L)
                .identificationType(IdentificationType.CC)
                .identificationNumber("987654321")
                .firstName("Jesus")
                .lastName("Marin")
                .email("jesumarin@gmail.com")
                .birthDate(LocalDate.of(1990, 5, 15))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateClientAndReturn201() throws Exception {
        when(mapper.toDomain(any(ClientRequest.class))).thenReturn(validClient);
        when(clientUseCase.create(any(Client.class))).thenReturn(validClient);
        when(mapper.toResponse(any(Client.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Jesus"))
                .andExpect(jsonPath("$.email").value("jesumarin@gmail.com"));
    }

    @Test
    void shouldReturn400WhenRequestHasInvalidFields() throws Exception {
        ClientRequest invalidRequest = new ClientRequest();
        invalidRequest.setFirstName("J");
        invalidRequest.setLastName("");
        invalidRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn422WhenClientIsUnderage() throws Exception {
        when(mapper.toDomain(any(ClientRequest.class))).thenReturn(validClient);
        when(clientUseCase.create(any(Client.class)))
                .thenThrow(new UnderageClientException(17));

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        when(mapper.toDomain(any(ClientRequest.class))).thenReturn(validClient);
        when(clientUseCase.create(any(Client.class)))
                .thenThrow(new DuplicateEmailException(validClient.getEmail()));

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentClient() throws Exception {
        when(mapper.toDomain(any(ClientRequest.class))).thenReturn(validClient);
        when(clientUseCase.update(eq(99L), any(Client.class)))
                .thenThrow(new ClientNotFoundException(99L));

        mockMvc.perform(put("/api/v1/clients/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldFindClientByIdentificationNumberAndReturn200() throws Exception {
        when(clientUseCase.findByIdentificationNumber("987654321"))
                .thenReturn(validClient);
        when(mapper.toResponse(any(Client.class))).thenReturn(validResponse);

        mockMvc.perform(get("/api/v1/clients/987654321"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identificationNumber").value("987654321"))
                .andExpect(jsonPath("$.firstName").value("Jesus"));
    }

    @Test
    void shouldReturn404WhenClientNotFoundByIdentificationNumber() throws Exception {
        when(clientUseCase.findByIdentificationNumber("0000000000"))
                .thenThrow(new ClientNotFoundException("0000000000"));

        mockMvc.perform(get("/api/v1/clients/0000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturnAllClientsAndReturn200() throws Exception {
        when(clientUseCase.findAll()).thenReturn(List.of(validClient));
        when(mapper.toResponse(any(Client.class))).thenReturn(validResponse);

        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldReturnEmptyListAndReturn200() throws Exception {
        when(clientUseCase.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    void shouldDeleteClientAndReturn204() throws Exception {
        doNothing().when(clientUseCase).delete(1L);

        mockMvc.perform(delete("/api/v1/clients/1"))
                .andExpect(status().isNoContent());

        verify(clientUseCase).delete(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentClient() throws Exception {
        doThrow(new ClientNotFoundException(99L))
                .when(clientUseCase).delete(99L);

        mockMvc.perform(delete("/api/v1/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn409WhenDeletingClientWithProducts() throws Exception {
        doThrow(new ClientHasProductsException(1L))
                .when(clientUseCase).delete(1L);

        mockMvc.perform(delete("/api/v1/clients/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }
}
