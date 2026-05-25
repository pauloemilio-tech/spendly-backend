package com.spendly.domain.entity;

import java.util.EnumSet;

public enum TransactionCategory {
    // INCOME
    SALARY(TransactionType.INCOME),
    FREELANCE(TransactionType.INCOME),
    INVESTMENT_RETURN(TransactionType.INCOME),
    GIFT(TransactionType.INCOME),
    OTHER_INCOME(TransactionType.INCOME),

    // EXPENSE
    FOOD(TransactionType.EXPENSE),
    TRANSPORT(TransactionType.EXPENSE),
    HEALTH(TransactionType.EXPENSE),
    EDUCATION(TransactionType.EXPENSE),
    ENTERTAINMENT(TransactionType.EXPENSE),
    SHOPPING(TransactionType.EXPENSE),
    BILLS(TransactionType.EXPENSE),
    INVESTMENT(TransactionType.EXPENSE),
    OTHER_EXPENSE(TransactionType.EXPENSE);

    private final TransactionType type;

    TransactionCategory(TransactionType type) {
        this.type = type;
    }

    public TransactionType getType() {
        return type;
    }

    public boolean isCompatibleWith(TransactionType other) {
        return this.type == other;
    }

    public static EnumSet<TransactionCategory> forType(TransactionType type) {
        EnumSet<TransactionCategory> set = EnumSet.noneOf(TransactionCategory.class);
        for (TransactionCategory c : values()) {
            if (c.type == type) set.add(c);
        }
        return set;
    }
}
