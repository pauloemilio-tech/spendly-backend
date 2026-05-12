package com.spendly.domain.exception;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(Long walletId) {
        super("Carteira não encontrada: " + walletId);
    }
}
