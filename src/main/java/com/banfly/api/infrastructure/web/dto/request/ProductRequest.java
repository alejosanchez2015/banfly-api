package com.banfly.api.infrastructure.web.dto.request;

import com.banfly.api.domain.product.model.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Client id is required")
    private Long clientId;

    @NotNull(message = "GMF exempt is required")
    private boolean gmfExempt;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", message = "Balance can't be negative")
    private BigDecimal balance;


}
