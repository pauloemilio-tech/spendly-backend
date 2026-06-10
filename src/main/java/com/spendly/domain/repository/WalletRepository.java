package com.spendly.domain.repository;

import com.spendly.domain.entity.Wallet;
import com.spendly.domain.entity.WalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findAllByCustomerId(Long customerId);

    List<Wallet> findAllByCustomerIdAndStatus(Long customerId, WalletStatus status);

    long countByCustomerIdAndStatus(Long customerId, WalletStatus status);

    @Query("""
            select coalesce(sum(w.balance), 0)
            from Wallet w
            where w.customer.id = :customerId
              and w.status = :status
            """)
    BigDecimal sumBalanceByCustomerIdAndStatus(
            @Param("customerId") Long customerId,
            @Param("status") WalletStatus status
    );

    Optional<Wallet> findByIdAndCustomerId(Long id, Long customerId);

    Optional<Wallet> findByIdAndCustomerIdAndStatus(Long id, Long customerId, WalletStatus status);
}
