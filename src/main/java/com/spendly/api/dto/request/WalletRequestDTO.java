package com.spendly.api.dto.request;

import com.spendly.domain.entity.WalletType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record WalletRequestDTO(

        @NotBlank(message = "O nome da carteira é obrigatório")
        @Size(min = 1, max = 50, message = "O nome deve ter entre 1 e 50 caracteres")
        String name,

        @NotNull(message = "O tipo da carteira é obrigatório")
        WalletType walletType,

        @DecimalMin(value = "0.00", message = "O saldo inicial não pode ser negativo")
        BigDecimal initialBalance
) {
}
