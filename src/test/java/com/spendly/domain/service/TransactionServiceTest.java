package com.spendly.domain.service;

import com.spendly.api.dto.request.TransactionRequestDTO;
import com.spendly.api.dto.response.TransactionResponseDTO;
import com.spendly.domain.entity.Customer;
import com.spendly.domain.entity.Transaction;
import com.spendly.domain.entity.TransactionCategory;
import com.spendly.domain.entity.TransactionType;
import com.spendly.domain.entity.TransactionStatus;
import com.spendly.domain.entity.Wallet;
import com.spendly.domain.entity.WalletStatus;
import com.spendly.domain.entity.WalletType;
import com.spendly.domain.exception.InsufficientFundsException;
import com.spendly.domain.exception.TransactionCategoryMismatchException;
import com.spendly.domain.exception.TransactionAlreadyReversedException;
import com.spendly.domain.exception.TransactionNotFoundException;
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
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final String CPF = "12345678901";
    private static final Long CUSTOMER_ID = 10L;
    private static final Long WALLET_ID = 20L;
    private static final Long TRANSACTION_ID = 30L;

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
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.ACTIVE);
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

    @Test
    void shouldReverseIncomeByDecreasingBalanceAndReturningReversedTransaction() {
        Wallet wallet = walletWithBalance("150.00");
        Transaction transaction = transaction(wallet, TransactionType.INCOME, TransactionCategory.SALARY, "50.00");
        stubOwnedTransaction(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        TransactionResponseDTO response = transactionService.reverseTransaction(CPF, TRANSACTION_ID);

        assertThat(wallet.getBalance()).isEqualByComparingTo("100.00");
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.REVERSED);
        assertThat(response.status()).isEqualTo(TransactionStatus.REVERSED);
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(same(transaction));
        verify(transactionRepository).findByIdAndWalletCustomerId(TRANSACTION_ID, CUSTOMER_ID);
    }

    @Test
    void shouldReverseExpenseByIncreasingBalanceAndReturningReversedTransaction() {
        Wallet wallet = walletWithBalance("60.00");
        Transaction transaction = transaction(wallet, TransactionType.EXPENSE, TransactionCategory.FOOD, "40.00");
        stubOwnedTransaction(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        TransactionResponseDTO response = transactionService.reverseTransaction(CPF, TRANSACTION_ID);

        assertThat(wallet.getBalance()).isEqualByComparingTo("100.00");
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.REVERSED);
        assertThat(response.status()).isEqualTo(TransactionStatus.REVERSED);
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(same(transaction));
        verify(transactionRepository).findByIdAndWalletCustomerId(TRANSACTION_ID, CUSTOMER_ID);
    }

    @Test
    void shouldRejectDuplicateReversalWithoutChangingBalanceOrSavingAgain() {
        Wallet wallet = walletWithBalance("100.00");
        Transaction transaction = transaction(wallet, TransactionType.EXPENSE, TransactionCategory.FOOD, "40.00");
        transaction.reverse();
        stubOwnedTransaction(transaction);

        assertThrows(TransactionAlreadyReversedException.class,
                () -> transactionService.reverseTransaction(CPF, TRANSACTION_ID));

        assertThat(wallet.getBalance()).isEqualByComparingTo("100.00");
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.REVERSED);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldRejectIncomeReversalWhenWalletBalanceIsInsufficient() {
        Wallet wallet = walletWithBalance("30.00");
        Transaction transaction = transaction(wallet, TransactionType.INCOME, TransactionCategory.SALARY, "50.00");
        stubOwnedTransaction(transaction);

        assertThrows(InsufficientFundsException.class,
                () -> transactionService.reverseTransaction(CPF, TRANSACTION_ID));

        assertThat(wallet.getBalance()).isEqualByComparingTo("30.00");
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.ACTIVE);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldThrowTransactionNotFoundWhenTransactionDoesNotExist() {
        stubCustomer();
        when(transactionRepository.findByIdAndWalletCustomerId(99L, CUSTOMER_ID))
                .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class,
                () -> transactionService.reverseTransaction(CPF, 99L));

        verify(transactionRepository).findByIdAndWalletCustomerId(99L, CUSTOMER_ID);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldTreatAnotherCustomersTransactionAsNotFound() {
        stubCustomer();
        when(transactionRepository.findByIdAndWalletCustomerId(88L, CUSTOMER_ID))
                .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class,
                () -> transactionService.reverseTransaction(CPF, 88L));

        verify(transactionRepository).findByIdAndWalletCustomerId(88L, CUSTOMER_ID);
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

    private void stubOwnedTransaction(Transaction transaction) {
        stubCustomer();
        when(transactionRepository.findByIdAndWalletCustomerId(TRANSACTION_ID, CUSTOMER_ID))
                .thenReturn(Optional.of(transaction));
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

    private Transaction transaction(Wallet wallet,
                                    TransactionType type,
                                    TransactionCategory category,
                                    String amount) {
        return new Transaction(wallet, type, category, new BigDecimal(amount), "Descrição");
    }
}
