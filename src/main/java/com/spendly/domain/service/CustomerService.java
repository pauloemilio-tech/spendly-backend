package com.spendly.domain.service;

import com.spendly.api.dto.request.CustomerRequestDTO;
import com.spendly.api.dto.response.CustomerResponseDTO;
import com.spendly.domain.entity.Customer;
import com.spendly.domain.exception.CpfAlreadyExistsException;
import com.spendly.domain.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CustomerResponseDTO registerNewCustomer(CustomerRequestDTO customerRequestDTO) {
        if (customerRepository.existsByCpf(customerRequestDTO.cpf())) {
            throw new CpfAlreadyExistsException("O CPF informado já foi cadastrado no sistema");
        }

        String passwordHash = passwordEncoder.encode(customerRequestDTO.password());
        Customer newCustomer = new Customer(customerRequestDTO.name(), customerRequestDTO.cpf(), passwordHash, customerRequestDTO.email());
        var savedCustomer = customerRepository.save(newCustomer);

        return new CustomerResponseDTO(savedCustomer.getId(), savedCustomer.getName(), savedCustomer.getEmail());
    }

    public Customer createGuestCustomer(GuestCredentials credentials) {
        String passwordHash = passwordEncoder.encode(credentials.password());
        Customer guest = new Customer(
                "Usuário Visitante",
                credentials.cpf(),
                passwordHash,
                credentials.email()
        );
        return customerRepository.saveAndFlush(guest);
    }

    public CustomerResponseDTO getCurrentCustomer(String cpf) {
        Customer customer = customerRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail()
        );
    }
}
