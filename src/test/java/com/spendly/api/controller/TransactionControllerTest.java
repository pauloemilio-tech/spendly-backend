package com.spendly.api.controller;

import com.spendly.api.dto.response.TransactionResponseDTO;
import com.spendly.domain.entity.TransactionStatus;
import com.spendly.domain.exception.InsufficientFundsException;
import com.spendly.domain.exception.TransactionAlreadyReversedException;
import com.spendly.domain.exception.TransactionNotFoundException;
import com.spendly.domain.repository.CustomerRepository;
import com.spendly.domain.repository.TransactionRepository;
import com.spendly.domain.repository.WalletRepository;
import com.spendly.domain.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@MockitoBean(types = {
        CustomerRepository.class,
        TransactionRepository.class,
        WalletRepository.class
})
class TransactionControllerTest {

    private static final String CPF = "12345678901";
    private static final Long TRANSACTION_ID = 30L;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private TransactionService transactionService;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldReverseTransactionWithoutRequestBody() throws Exception {
        when(transactionService.reverseTransaction(CPF, TRANSACTION_ID))
                .thenReturn(reversedResponse());

        mockMvc.perform(post("/transactions/{id}/reverse", TRANSACTION_ID)
                        .with(user(CPF)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(TRANSACTION_ID))
                .andExpect(jsonPath("$.status").value(TransactionStatus.REVERSED.name()));

        verify(transactionService).reverseTransaction(CPF, TRANSACTION_ID);
    }

    @Test
    void shouldReturnUnauthorizedWhenRequestIsNotAuthenticated() throws Exception {
        mockMvc.perform(post("/transactions/{id}/reverse", TRANSACTION_ID))
                .andExpect(status().isUnauthorized());

        verify(transactionService, never()).reverseTransaction(CPF, TRANSACTION_ID);
    }

    @Test
    void shouldReturnNotFoundWhenTransactionDoesNotExistOrBelongToCustomer() throws Exception {
        when(transactionService.reverseTransaction(CPF, TRANSACTION_ID))
                .thenThrow(new TransactionNotFoundException(TRANSACTION_ID));

        mockMvc.perform(post("/transactions/{id}/reverse", TRANSACTION_ID)
                        .with(user(CPF)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturnConflictWhenTransactionIsAlreadyReversed() throws Exception {
        when(transactionService.reverseTransaction(CPF, TRANSACTION_ID))
                .thenThrow(new TransactionAlreadyReversedException(TRANSACTION_ID));

        mockMvc.perform(post("/transactions/{id}/reverse", TRANSACTION_ID)
                        .with(user(CPF)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shouldReturnBadRequestWhenIncomeReversalHasInsufficientFunds() throws Exception {
        when(transactionService.reverseTransaction(CPF, TRANSACTION_ID))
                .thenThrow(new InsufficientFundsException(
                        "Insufficient wallet balance to reverse income transaction"
                ));

        mockMvc.perform(post("/transactions/{id}/reverse", TRANSACTION_ID)
                        .with(user(CPF)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private TransactionResponseDTO reversedResponse() {
        return new TransactionResponseDTO(
                TRANSACTION_ID,
                20L,
                "EXPENSE",
                "FOOD",
                new BigDecimal("40.00"),
                "Almoço",
                LocalDateTime.of(2026, 8, 4, 10, 0),
                TransactionStatus.REVERSED
        );
    }
}
