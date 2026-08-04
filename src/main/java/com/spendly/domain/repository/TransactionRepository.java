package com.spendly.domain.repository;

import com.spendly.domain.entity.Transaction;
import com.spendly.domain.entity.TransactionStatus;
import com.spendly.domain.entity.TransactionType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByWalletCustomerId(Long customerId);

    long countByWalletCustomerId(Long customerId);

    @Query("""
            select coalesce(sum(t.amount), 0)
            from Transaction t
            where t.wallet.customer.id = :customerId
              and t.type = :type
              and t.status = :status
            """)
    BigDecimal sumAmountByCustomerIdAndTypeAndStatus(
            @Param("customerId") Long customerId,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status
    );

    @EntityGraph(attributePaths = "wallet")
    List<Transaction> findTop5ByWalletCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<Transaction> findByIdAndWalletCustomerId(Long id, Long customerId);
}

