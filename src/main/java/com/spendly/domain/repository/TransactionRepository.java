package com.spendly.domain.repository;

import com.spendly.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByWalletCustomerId(Long customerId);

    Optional<Transaction> findByIdAndWalletCustomerId(Long id, Long customerId);
}

