package com.banfly.api.client.infraestructure.web.dto.request;

import com.banfly.api.client.domain.model.IdentificationType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClientRequest {

    @NotNull(message = "Identification type is required")
    private IdentificationType identificationType;

    @NotBlank(message = "Identification number is required")
    private String identificationNumber;

    @NotBlank(message = "First name is required")
    @Size(min = 2, message = "First name must be at least 2 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, message = "Last name must be at least 2 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be a past date")
    private LocalDate birthDate;

}
