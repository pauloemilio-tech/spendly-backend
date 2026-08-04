package com.spendly.domain.service;

import com.spendly.api.dto.response.DashboardSummaryResponse;
import com.spendly.api.dto.response.RecentTransactionResponse;
import com.spendly.domain.entity.Customer;
import com.spendly.domain.entity.TransactionStatus;
import com.spendly.domain.entity.TransactionType;
import com.spendly.domain.entity.WalletStatus;
import com.spendly.domain.repository.CustomerRepository;
import com.spendly.domain.repository.TransactionRepository;
import com.spendly.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public DashboardService(
            CustomerRepository customerRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository
    ) {
        this.customerRepository = customerRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary(String cpf) {
        Customer customer = customerRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Cliente autenticado não encontrado"));

        Long customerId = customer.getId();
        BigDecimal totalBalance = zeroIfNull(
                walletRepository.sumBalanceByCustomerIdAndStatus(customerId, WalletStatus.ACTIVE)
        );
        BigDecimal totalIncome = zeroIfNull(
                transactionRepository.sumAmountByCustomerIdAndTypeAndStatus(
                        customerId,
                        TransactionType.INCOME,
                        TransactionStatus.ACTIVE
                )
        );
        BigDecimal totalExpense = zeroIfNull(
                transactionRepository.sumAmountByCustomerIdAndTypeAndStatus(
                        customerId,
                        TransactionType.EXPENSE,
                        TransactionStatus.ACTIVE
                )
        );
        long walletCount = walletRepository.countByCustomerIdAndStatus(customerId, WalletStatus.ACTIVE);
        long transactionCount = transactionRepository.countByWalletCustomerId(customerId);
        List<RecentTransactionResponse> recentTransactions =
                transactionRepository.findTop5ByWalletCustomerIdOrderByCreatedAtDesc(customerId)
                        .stream()
                        .map(RecentTransactionResponse::from)
                        .toList();

        return new DashboardSummaryResponse(
                totalBalance,
                totalIncome,
                totalExpense,
                walletCount,
                transactionCount,
                recentTransactions
        );
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
