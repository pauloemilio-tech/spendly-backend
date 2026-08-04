package com.spendly.api.controller;

import com.spendly.api.dto.request.LoginRequestDTO;
import com.spendly.api.dto.response.LoginResponseDTO;
import com.spendly.domain.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO data) {
        return authService.login(data);
    }

    @PostMapping("/auth/guest")
    public LoginResponseDTO createGuestSession() {
        return authService.createGuestSession();
    }
}
