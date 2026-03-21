package com.banfly.api.client.infraestructure.web.dto.response;

import com.banfly.api.client.domain.model.IdentificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ClientResponse {

    private Long id;
    private IdentificationType identificationType;
    private String identificationNumber;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
