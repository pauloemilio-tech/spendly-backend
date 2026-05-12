package com.spendly.domain.exception;

public class WalletAccessDeniedException extends RuntimeException {
    public WalletAccessDeniedException() {
        super("Acesso negado: você não tem permissão para acessar esta carteira");
    }
}
