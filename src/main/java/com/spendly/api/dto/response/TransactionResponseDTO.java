package com.spendly.api.dto.response;

import com.spendly.domain.entity.Transaction;
import com.spendly.domain.entity.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long id,
        Long walletId,
        String type,
    String category,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt,
        TransactionStatus status
) {
    public static TransactionResponseDTO from(Transaction t) {
        return new TransactionResponseDTO(
                t.getId(),
                t.getWallet().getId(),
                t.getType().name(),
        t.getCategory() != null ? t.getCategory().name() : null,
                t.getAmount(),
                t.getDescription(),
                t.getCreatedAt(),
                t.getStatus()
        );
    }
}
