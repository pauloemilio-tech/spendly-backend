package com.spendly.domain.entity;

import com.spendly.api.dto.response.RecentTransactionResponse;
import com.spendly.api.dto.response.TransactionResponseDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionTest {

    @Test
    void shouldStartAsActiveAndMapStatusToResponseDtos() {
        Transaction transaction = newTransaction();

        TransactionResponseDTO transactionResponse = TransactionResponseDTO.from(transaction);
        RecentTransactionResponse recentResponse = RecentTransactionResponse.from(transaction);

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.ACTIVE);
        assertThat(transactionResponse.status()).isEqualTo(TransactionStatus.ACTIVE);
        assertThat(recentResponse.status()).isEqualTo(TransactionStatus.ACTIVE);
    }

    @Test
    void shouldReverseAndMapReversedStatusToResponseDtos() {
        Transaction transaction = newTransaction();

        transaction.reverse();

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.REVERSED);
        assertThat(TransactionResponseDTO.from(transaction).status()).isEqualTo(TransactionStatus.REVERSED);
        assertThat(RecentTransactionResponse.from(transaction).status()).isEqualTo(TransactionStatus.REVERSED);
    }

    private Transaction newTransaction() {
        Customer customer = new Customer("Cliente", "12345678901", "hash", "cliente@spendly.com");
        Wallet wallet = new Wallet(
                "Principal",
                WalletType.BANK_ACCOUNT,
                BigDecimal.ZERO,
                customer
        );
        return new Transaction(
                wallet,
                TransactionType.INCOME,
                TransactionCategory.SALARY,
                new BigDecimal("100.00"),
                "Salário"
        );
    }
}
