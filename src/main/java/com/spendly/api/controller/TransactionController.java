package com.spendly.api.controller;

import com.spendly.api.dto.request.TransactionRequestDTO;
import com.spendly.api.dto.response.TransactionResponseDTO;
import com.spendly.domain.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @Valid @RequestBody TransactionRequestDTO dto,
            Authentication authentication) {
        TransactionResponseDTO resp = transactionService.createTransaction(authentication.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping
    public List<TransactionResponseDTO> listTransactions(Authentication authentication) {
        return transactionService.listTransactions(authentication.getName());
    }

    @GetMapping("/{id}")
    public TransactionResponseDTO getTransaction(
            @PathVariable Long id,
            Authentication authentication) {
        return transactionService.getTransaction(authentication.getName(), id);
    }

    @PostMapping("/{id}/reverse")
    public ResponseEntity<TransactionResponseDTO> reverseTransaction(
            @PathVariable Long id,
            Authentication authentication) {
        TransactionResponseDTO response =
                transactionService.reverseTransaction(authentication.getName(), id);

        return ResponseEntity.ok(response);
    }
}
