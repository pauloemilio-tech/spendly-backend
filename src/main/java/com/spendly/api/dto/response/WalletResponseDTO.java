package com.spendly.api.dto.response;

import com.spendly.domain.entity.Wallet;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WalletResponseDTO(
        Long id,
        String name,
        BigDecimal balance,
        String walletType,
        String status,
        LocalDate openingDate
) {
    public static WalletResponseDTO from(Wallet wallet) {
        return new WalletResponseDTO(
                wallet.getId(),
                wallet.getName(),
                wallet.getBalance(),
                wallet.getWalletType().getDescription(),
                wallet.getStatus().name(),
                wallet.getOpeningDate()
        );
    }
}
