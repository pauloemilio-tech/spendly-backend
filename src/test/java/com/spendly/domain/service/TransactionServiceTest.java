package com.spendly.domain.service;

import com.spendly.api.dto.request.TransactionRequestDTO;
import com.spendly.domain.entity.Customer;
import com.spendly.domain.entity.Transaction;
import com.spendly.domain.entity.TransactionCategory;
import com.spendly.domain.entity.TransactionType;
import com.spendly.domain.entity.Wallet;
import com.spendly.domain.entity.WalletStatus;
import com.spendly.domain.entity.WalletType;
import com.spendly.domain.exception.InsufficientFundsException;
import com.spendly.domain.exception.TransactionCategoryMismatchException;
import com.spendly.domain.exception.WalletNotFoundException;
import com.spendly.domain.repository.CustomerRepository;
import com.spendly.domain.repository.TransactionRepository;
import com.spendly.domain.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final String CPF = "12345678901";
    private static final Long CUSTOMER_ID = 10L;
    private static final Long WALLET_ID = 20L;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldIncreaseWalletBalanceWhenIncomeIsValid() {
        Wallet wallet = walletWithBalance("100.00");
        TransactionRequestDTO request = request(TransactionType.INCOME, TransactionCategory.SALARY, "50.00");
        stubCustomerAndActiveWallet(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.createTransaction(CPF, request);

        assertThat(wallet.getBalance()).isEqualByComparingTo("150.00");
        verify(walletRepository).save(wallet);
    }

    @Test
    void shouldDecreaseWalletBalanceWhenExpenseIsValid() {
        Wallet wallet = walletWithBalance("100.00");
        TransactionRequestDTO request = request(TransactionType.EXPENSE, TransactionCategory.FOOD, "40.00");
        stubCustomerAndActiveWallet(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.createTransaction(CPF, request);

        assertThat(wallet.getBalance()).isEqualByComparingTo("60.00");
        verify(walletRepository).save(wallet);
    }

    @Test
    void shouldThrowInsufficientFundsWhenExpenseExceedsWalletBalance() {
        Wallet wallet = walletWithBalance("30.00");
        TransactionRequestDTO request = request(TransactionType.EXPENSE, TransactionCategory.FOOD, "40.00");
        stubCustomerAndActiveWallet(wallet);

        assertThrows(InsufficientFundsException.class,
                () -> transactionService.createTransaction(CPF, request));

        assertThat(wallet.getBalance()).isEqualByComparingTo("30.00");
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldRejectTransactionWhenNoActiveWalletBelongingToAuthenticatedCustomerIsFound() {
        stubCustomer();
        TransactionRequestDTO request = request(TransactionType.INCOME, TransactionCategory.SALARY, "50.00");
        when(walletRepository.findByIdAndCustomerIdAndStatus(WALLET_ID, CUSTOMER_ID, WalletStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class,
                () -> transactionService.createTransaction(CPF, request));

        verify(walletRepository).findByIdAndCustomerIdAndStatus(
                WALLET_ID,
                CUSTOMER_ID,
                WalletStatus.ACTIVE
        );
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldRejectCategoryThatIsIncompatibleWithTransactionType() {
        Wallet wallet = walletWithBalance("100.00");
        TransactionRequestDTO request = request(TransactionType.EXPENSE, TransactionCategory.SALARY, "10.00");
        stubCustomerAndActiveWallet(wallet);

        assertThrows(TransactionCategoryMismatchException.class,
                () -> transactionService.createTransaction(CPF, request));

        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldSaveValidTransactionWithRequestData() {
        Wallet wallet = walletWithBalance("100.00");
        TransactionRequestDTO request = new TransactionRequestDTO(
                WALLET_ID,
                TransactionType.INCOME,
                TransactionCategory.FREELANCE,
                new BigDecimal("75.50"),
                "Projeto"
        );
        stubCustomerAndActiveWallet(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        transactionService.createTransaction(CPF, request);

        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction transaction = transactionCaptor.getValue();
        assertThat(transaction.getWallet()).isSameAs(wallet);
        assertThat(transaction.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(transaction.getCategory()).isEqualTo(TransactionCategory.FREELANCE);
        assertThat(transaction.getAmount()).isEqualByComparingTo("75.50");
        assertThat(transaction.getDescription()).isEqualTo("Projeto");
    }

    @Test
    void shouldNotSaveTransactionWhenAmountIsNotPositive() {
        Wallet wallet = walletWithBalance("100.00");
        TransactionRequestDTO request = request(TransactionType.INCOME, TransactionCategory.SALARY, "0.00");
        stubCustomerAndActiveWallet(wallet);

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.createTransaction(CPF, request));

        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    private void stubCustomerAndActiveWallet(Wallet wallet) {
        stubCustomer();
        when(walletRepository.findByIdAndCustomerIdAndStatus(WALLET_ID, CUSTOMER_ID, WalletStatus.ACTIVE))
                .thenReturn(Optional.of(wallet));
    }

    private Customer stubCustomer() {
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(CUSTOMER_ID);
        when(customerRepository.findByCpf(CPF)).thenReturn(Optional.of(customer));
        return customer;
    }

    private Wallet walletWithBalance(String balance) {
        Customer owner = new Customer("Cliente", CPF, "hash", "cliente@spendly.com");
        return new Wallet("Principal", WalletType.BANK_ACCOUNT, new BigDecimal(balance), owner);
    }

    private TransactionRequestDTO request(TransactionType type,
                                          TransactionCategory category,
                                          String amount) {
        return new TransactionRequestDTO(WALLET_ID, type, category, new BigDecimal(amount), "Descrição");
    }
}
