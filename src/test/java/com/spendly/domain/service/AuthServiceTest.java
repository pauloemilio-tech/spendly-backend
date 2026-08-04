package com.spendly.domain.service;

import com.spendly.domain.exception.GuestAccountCreationException;
import com.spendly.domain.repository.CustomerRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Test
    void shouldFailWithControlledExceptionAfterLimitedIdentifierCollisions() {
        CustomerRepository repository = mock(CustomerRepository.class);
        CustomerService customerService = mock(CustomerService.class);
        GuestCredentialsGenerator generator = mock(GuestCredentialsGenerator.class);
        GuestCredentials credentials = new GuestCredentials(
                "52998224725", "guest-collision@spendly.demo", "secret"
        );
        when(generator.generate()).thenReturn(credentials);
        when(repository.existsByCpf(credentials.cpf())).thenReturn(true);

        AuthService service = new AuthService(
                repository, mock(org.springframework.security.crypto.password.PasswordEncoder.class),
                mock(JwtService.class), customerService, generator
        );

        assertThatThrownBy(service::createGuestSession)
                .isInstanceOf(GuestAccountCreationException.class)
                .hasMessage("Não foi possível criar a sessão de visitante");
        verifyNoInteractions(customerService);
    }
}
