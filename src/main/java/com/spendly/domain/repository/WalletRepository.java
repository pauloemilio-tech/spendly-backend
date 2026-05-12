package com.spendly.domain.repository;

import com.spendly.domain.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findAllByCustomerId(Long customerId);

    Optional<Wallet> findByIdAndCustomerId(Long id, Long customerId);
}
