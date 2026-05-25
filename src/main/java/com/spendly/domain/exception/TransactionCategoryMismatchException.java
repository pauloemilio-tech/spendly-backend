package com.spendly.domain.exception;

import com.spendly.domain.entity.TransactionCategory;
import com.spendly.domain.entity.TransactionType;

public class TransactionCategoryMismatchException extends RuntimeException {
    public TransactionCategoryMismatchException(TransactionType type, TransactionCategory category) {
        super("Transaction category '" + category + "' is not compatible with transaction type '" + type + "'");
    }
}
