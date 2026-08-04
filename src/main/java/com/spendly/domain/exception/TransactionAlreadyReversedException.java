package com.spendly.domain.exception;

public class TransactionAlreadyReversedException extends RuntimeException {

    public TransactionAlreadyReversedException(Long id) {
        super("Transaction has already been reversed: " + id);
    }
}
