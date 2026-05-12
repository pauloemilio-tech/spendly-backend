package com.spendly.domain.entity;

public enum WalletType {
    CHECKING("Conta Corrente"),
    SAVINGS("Poupança"),
    INVESTMENT("Investimentos"),
    CASH("Dinheiro em Espécie"),
    DIGITAL("Carteira Digital");

    private final String description;

    WalletType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}