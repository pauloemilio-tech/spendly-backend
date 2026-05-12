package com.spendly.api.dto.request;

import com.spendly.domain.entity.WalletType;
import jakarta.validation.constraints.Size;

public record WalletUpdateRequestDTO(

        @Size(min = 1, max = 50, message = "O nome deve ter entre 1 e 50 caracteres")
        String name,

        WalletType walletType
) {
}
