package com.spendly.domain.exception;

public class GuestAccountCreationException extends RuntimeException {
    public GuestAccountCreationException(String message) {
        super(message);
    }
}
