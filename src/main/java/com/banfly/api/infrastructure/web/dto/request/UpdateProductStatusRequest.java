package com.banfly.api.infrastructure.web.dto.request;

import com.banfly.api.domain.product.model.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductStatusRequest {

    @NotNull(message = "Status is required")
    private AccountStatus status;

}
