package com.spendly.domain.repository;

import com.spendly.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    public boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    Optional<Customer> findByCpf(String cpf);
}
