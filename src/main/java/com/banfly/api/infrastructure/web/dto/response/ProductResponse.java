package com.banfly.api.infrastructure.web.dto.response;

import com.banfly.api.domain.product.model.AccountStatus;
import com.banfly.api.domain.product.model.AccountType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private AccountType accountType;
    private String accountNumber;
    private AccountStatus status;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private boolean gmfExempt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long clientId;

}
