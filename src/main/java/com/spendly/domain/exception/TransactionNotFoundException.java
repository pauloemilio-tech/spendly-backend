package com.spendly.domain.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long id) {
        super("Transação não encontrada: " + id);
    }
}
