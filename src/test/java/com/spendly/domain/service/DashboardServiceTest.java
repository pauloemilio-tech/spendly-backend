package com.spendly.domain.service;

import com.spendly.api.dto.response.DashboardSummaryResponse;
import com.spendly.api.dto.response.RecentTransactionResponse;
import com.spendly.domain.entity.Customer;
import com.spendly.domain.entity.Transaction;
import com.spendly.domain.entity.TransactionCategory;
import com.spendly.domain.entity.TransactionType;
import com.spendly.domain.entity.Wallet;
import com.spendly.domain.entity.WalletStatus;
import com.spendly.domain.entity.WalletType;
import com.spendly.domain.repository.CustomerRepository;
import com.spendly.domain.repository.TransactionRepository;
import com.spendly.domain.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    private static final String CPF = "12345678901";
    private static final Long CUSTOMER_ID = 10L;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void shouldReturnSummaryWithAggregatedValuesForAuthenticatedCustomer() {
        stubCustomer();
        when(walletRepository.sumBalanceByCustomerIdAndStatus(CUSTOMER_ID, WalletStatus.ACTIVE))
                .thenReturn(new BigDecimal("1250.75"));
        when(transactionRepository.sumAmountByCustomerIdAndType(CUSTOMER_ID, TransactionType.INCOME))
                .thenReturn(new BigDecimal("3000.00"));
        when(transactionRepository.sumAmountByCustomerIdAndType(CUSTOMER_ID, TransactionType.EXPENSE))
                .thenReturn(new BigDecimal("1749.25"));
        when(walletRepository.countByCustomerIdAndStatus(CUSTOMER_ID, WalletStatus.ACTIVE)).thenReturn(2L);
        when(transactionRepository.countByWalletCustomerId(CUSTOMER_ID)).thenReturn(8L);
        when(transactionRepository.findTop5ByWalletCustomerIdOrderByCreatedAtDesc(CUSTOMER_ID))
                .thenReturn(List.of());

        DashboardSummaryResponse response = dashboardService.getSummary(CPF);

        assertThat(response.totalBalance()).isEqualByComparingTo("1250.75");
        assertThat(response.totalIncome()).isEqualByComparingTo("3000.00");
        assertThat(response.totalExpense()).isEqualByComparingTo("1749.25");
        assertThat(response.walletCount()).isEqualTo(2L);
        assertThat(response.transactionCount()).isEqualTo(8L);
        assertThat(response.recentTransactions()).isEmpty();
        verify(walletRepository).sumBalanceByCustomerIdAndStatus(CUSTOMER_ID, WalletStatus.ACTIVE);
        verify(transactionRepository).sumAmountByCustomerIdAndType(CUSTOMER_ID, TransactionType.INCOME);
        verify(transactionRepository).sumAmountByCustomerIdAndType(CUSTOMER_ID, TransactionType.EXPENSE);
        verify(walletRepository).countByCustomerIdAndStatus(CUSTOMER_ID, WalletStatus.ACTIVE);
        verify(transactionRepository).countByWalletCustomerId(CUSTOMER_ID);
    }

    @Test
    void shouldMapRecentTransactionsInRepositoryOrderUsingTopFiveQuery() {
        stubCustomer();
        Customer owner = new Customer("Cliente", CPF, "hash", "cliente@spendly.com");
        Wallet wallet = new Wallet("Principal", WalletType.BANK_ACCOUNT, BigDecimal.ZERO, owner);
        Transaction newest = new Transaction(
                wallet,
                TransactionType.EXPENSE,
                TransactionCategory.FOOD,
                new BigDecimal("35.90"),
                "Almoço"
        );
        Transaction older = new Transaction(
                wallet,
                TransactionType.INCOME,
                TransactionCategory.FREELANCE,
                new BigDecimal("500.00"),
                "Projeto"
        );
        when(transactionRepository.findTop5ByWalletCustomerIdOrderByCreatedAtDesc(CUSTOMER_ID))
                .thenReturn(List.of(newest, older));

        DashboardSummaryResponse response = dashboardService.getSummary(CPF);

        assertThat(response.recentTransactions())
                .extracting(RecentTransactionResponse::description)
                .containsExactly("Almoço", "Projeto");
        assertThat(response.recentTransactions().get(0).amount()).isEqualByComparingTo("35.90");
        assertThat(response.recentTransactions().get(0).type()).isEqualTo(TransactionType.EXPENSE.name());
        assertThat(response.recentTransactions().get(0).category()).isEqualTo(TransactionCategory.FOOD.name());
        assertThat(response.recentTransactions().get(0).walletName()).isEqualTo("Principal");
        verify(transactionRepository).findTop5ByWalletCustomerIdOrderByCreatedAtDesc(CUSTOMER_ID);
    }

    @Test
    void shouldReturnZeroValuesAndNoRecentTransactionsWhenRepositoriesHaveNoData() {
        stubCustomer();
        when(walletRepository.sumBalanceByCustomerIdAndStatus(CUSTOMER_ID, WalletStatus.ACTIVE))
                .thenReturn(null);
        when(transactionRepository.sumAmountByCustomerIdAndType(CUSTOMER_ID, TransactionType.INCOME))
                .thenReturn(null);
        when(transactionRepository.sumAmountByCustomerIdAndType(CUSTOMER_ID, TransactionType.EXPENSE))
                .thenReturn(null);
        when(walletRepository.countByCustomerIdAndStatus(CUSTOMER_ID, WalletStatus.ACTIVE)).thenReturn(0L);
        when(transactionRepository.countByWalletCustomerId(CUSTOMER_ID)).thenReturn(0L);
        when(transactionRepository.findTop5ByWalletCustomerIdOrderByCreatedAtDesc(CUSTOMER_ID))
                .thenReturn(List.of());

        DashboardSummaryResponse response = dashboardService.getSummary(CPF);

        assertThat(response.totalBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.totalIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.totalExpense()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.walletCount()).isZero();
        assertThat(response.transactionCount()).isZero();
        assertThat(response.recentTransactions()).isEmpty();
    }

    private void stubCustomer() {
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(CUSTOMER_ID);
        when(customerRepository.findByCpf(CPF)).thenReturn(Optional.of(customer));
    }
}
