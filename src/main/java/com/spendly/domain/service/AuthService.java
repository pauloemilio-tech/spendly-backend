package com.spendly.domain.service;

import com.spendly.api.dto.request.LoginRequestDTO;
import com.spendly.api.dto.response.LoginResponseDTO;
import com.spendly.domain.entity.Customer;
import com.spendly.domain.exception.InvalidCredentialsException;
import com.spendly.domain.exception.GuestAccountCreationException;
import com.spendly.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_GUEST_CREATION_ATTEMPTS = 5;

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomerService customerService;
    private final GuestCredentialsGenerator guestCredentialsGenerator;

    public LoginResponseDTO login(LoginRequestDTO data) {
        Customer customer = customerRepository.findByCpf(data.cpf())
                .orElseThrow(() -> new InvalidCredentialsException("CPF ou senha inválidos"));

        boolean passwordMatches = passwordEncoder.matches(data.password(), customer.getPasswordHash());

        if (!passwordMatches) {
            throw new InvalidCredentialsException("CPF ou senha inválidos");
        }

        return createLoginResponse(customer);
    }

    public LoginResponseDTO createGuestSession() {
        for (int attempt = 0; attempt < MAX_GUEST_CREATION_ATTEMPTS; attempt++) {
            GuestCredentials credentials = guestCredentialsGenerator.generate();
            if (customerRepository.existsByCpf(credentials.cpf())
                    || customerRepository.existsByEmail(credentials.email())) {
                continue;
            }

            try {
                return createLoginResponse(customerService.createGuestCustomer(credentials));
            } catch (DataIntegrityViolationException ignored) {
                // A concurrent insert may win after the checks; regenerate both identifiers.
            }
        }

        throw new GuestAccountCreationException("Não foi possível criar a sessão de visitante");
    }

    private LoginResponseDTO createLoginResponse(Customer customer) {
        String token = jwtService.generateToken(customer);
        return new LoginResponseDTO(token, "Bearer");
    }
}
