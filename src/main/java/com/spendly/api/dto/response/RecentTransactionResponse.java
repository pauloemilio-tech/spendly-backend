package com.spendly.api.dto.response;

import com.spendly.domain.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecentTransactionResponse(
        Long id,
        String description,
        BigDecimal amount,
        String type,
        String category,
        String walletName,
        LocalDateTime createdAt
) {
    public static RecentTransactionResponse from(Transaction transaction) {
        return new RecentTransactionResponse(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType().name(),
                transaction.getCategory().name(),
                transaction.getWallet().getName(),
                transaction.getCreatedAt()
        );
    }
}
