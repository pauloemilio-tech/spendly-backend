package com.spendly.domain.entity;

public enum TransactionStatus {
    ACTIVE("Transação ativa"),
    REVERSED("Transação estornada");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }
}
