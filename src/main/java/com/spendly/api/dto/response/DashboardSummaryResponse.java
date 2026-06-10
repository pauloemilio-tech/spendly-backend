package com.spendly.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        BigDecimal totalBalance,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        long walletCount,
        long transactionCount,
        List<RecentTransactionResponse> recentTransactions
) {
}
