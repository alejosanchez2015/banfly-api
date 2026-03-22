package com.banfly.api.infrastructure.web.dto.response;

import com.banfly.api.domain.transaction.model.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionResponse {

    private Long id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private Long sourceAccountId;
    private Long targetAccountId;
    private String description;

}
