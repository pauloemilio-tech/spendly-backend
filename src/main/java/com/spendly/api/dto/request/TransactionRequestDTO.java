package com.spendly.api.dto.request;

import com.spendly.domain.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequestDTO(

        @NotNull(message = "O id da wallet é obrigatório")
        Long walletId,

        @NotNull(message = "O tipo da transação é obrigatório")
        TransactionType type,

        @NotNull(message = "O valor é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        BigDecimal amount,

        String description
)
{
}
