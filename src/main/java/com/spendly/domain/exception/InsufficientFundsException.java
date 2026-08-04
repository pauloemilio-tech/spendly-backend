package com.spendly.domain.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super("Saldo insuficiente na carteira para realizar a despesa");
    }

    public InsufficientFundsException(String message) {
        super(message);
    }
}
