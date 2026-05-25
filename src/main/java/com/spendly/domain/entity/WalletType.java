package com.spendly.domain.entity;

public enum WalletType {
    BANK_ACCOUNT("Conta Bancária"),
    CASH("Dinheiro em Espécie"),
    CREDIT_CARD("Cartão de Crédito"),
    INVESTMENT("Investimentos"),
    DIGITAL_WALLET("Carteira Digital");

    private final String description;

    WalletType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}