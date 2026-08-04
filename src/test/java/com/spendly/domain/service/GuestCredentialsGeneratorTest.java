package com.spendly.domain.service;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GuestCredentialsGeneratorTest {

    private final GuestCredentialsGenerator generator = new GuestCredentialsGenerator();

    @Test
    void shouldGenerateUniqueValidCredentials() {
        Set<String> cpfs = new HashSet<>();
        Set<String> emails = new HashSet<>();

        for (int index = 0; index < 100; index++) {
            GuestCredentials credentials = generator.generate();
            assertThat(isValidCpf(credentials.cpf())).isTrue();
            assertThat(credentials.email()).matches("guest-[0-9a-f-]{36}@spendly\\.demo");
            assertThat(credentials.password()).hasSizeGreaterThanOrEqualTo(32);
            cpfs.add(credentials.cpf());
            emails.add(credentials.email());
        }

        assertThat(cpfs).hasSize(100);
        assertThat(emails).hasSize(100);
    }

    private boolean isValidCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) {
            return false;
        }
        return checkDigit(cpf, 9, 10) == Character.digit(cpf.charAt(9), 10)
                && checkDigit(cpf, 10, 11) == Character.digit(cpf.charAt(10), 10);
    }

    private int checkDigit(String cpf, int length, int weight) {
        int sum = 0;
        for (int index = 0; index < length; index++) {
            sum += Character.digit(cpf.charAt(index), 10) * (weight - index);
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
