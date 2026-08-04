package com.spendly.domain.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Component
public class GuestCredentialsGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    public GuestCredentials generate() {
        byte[] passwordBytes = new byte[24];
        secureRandom.nextBytes(passwordBytes);
        String password = Base64.getUrlEncoder().withoutPadding().encodeToString(passwordBytes);
        String email = "guest-" + UUID.randomUUID() + "@spendly.demo";
        return new GuestCredentials(generateCpf(), email, password);
    }

    private String generateCpf() {
        int[] digits = new int[11];
        do {
            for (int index = 0; index < 9; index++) {
                digits[index] = secureRandom.nextInt(10);
            }
        } while (allEqual(digits));

        digits[9] = calculateCheckDigit(digits, 9, 10);
        digits[10] = calculateCheckDigit(digits, 10, 11);

        StringBuilder cpf = new StringBuilder(11);
        for (int digit : digits) {
            cpf.append(digit);
        }
        return cpf.toString();
    }

    private boolean allEqual(int[] digits) {
        for (int index = 1; index < 9; index++) {
            if (digits[index] != digits[0]) {
                return false;
            }
        }
        return true;
    }

    private int calculateCheckDigit(int[] digits, int length, int initialWeight) {
        int sum = 0;
        for (int index = 0; index < length; index++) {
            sum += digits[index] * (initialWeight - index);
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
