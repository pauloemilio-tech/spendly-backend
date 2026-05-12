package com.spendly.api.controller;

import com.spendly.api.dto.request.WalletRequestDTO;
import com.spendly.api.dto.request.WalletUpdateRequestDTO;
import com.spendly.api.dto.response.WalletResponseDTO;
import com.spendly.domain.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponseDTO> createWallet(
            @Valid @RequestBody WalletRequestDTO dto,
            Authentication authentication) {
        WalletResponseDTO response = walletService.createWallet(authentication.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<WalletResponseDTO> listWallets(Authentication authentication) {
        return walletService.listWallets(authentication.getName());
    }

    @GetMapping("/{id}")
    public WalletResponseDTO getWallet(
            @PathVariable Long id,
            Authentication authentication) {
        return walletService.getWallet(authentication.getName(), id);
    }

    @PatchMapping("/{id}")
    public WalletResponseDTO updateWallet(
            @PathVariable Long id,
            @Valid @RequestBody WalletUpdateRequestDTO dto,
            Authentication authentication) {
        return walletService.updateWallet(authentication.getName(), id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateWallet(
            @PathVariable Long id,
            Authentication authentication) {
        walletService.deactivateWallet(authentication.getName(), id);
    }
}
