package com.banfly.api.domain.transaction.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private Long id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private Long sourceAccountId;
    private Long targetAccountId;
    private String description;

}
