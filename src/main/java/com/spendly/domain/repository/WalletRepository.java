package com.spendly.domain.repository;

import com.spendly.domain.entity.Wallet;
import com.spendly.domain.entity.WalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findAllByCustomerId(Long customerId);

    List<Wallet> findAllByCustomerIdAndStatus(Long customerId, WalletStatus status);

    Optional<Wallet> findByIdAndCustomerId(Long id, Long customerId);

    Optional<Wallet> findByIdAndCustomerIdAndStatus(Long id, Long customerId, WalletStatus status);
}
