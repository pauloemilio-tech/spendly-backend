package com.spendly.api.controller;

import com.spendly.domain.entity.Customer;
import com.spendly.domain.repository.CustomerRepository;
import com.spendly.domain.repository.TransactionRepository;
import com.spendly.domain.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class GuestAuthControllerTest {

    @Autowired private WebApplicationContext context;
    @Autowired private PasswordEncoder passwordEncoder;
    @MockitoBean private CustomerRepository customerRepository;
    @MockitoBean private WalletRepository walletRepository;
    @MockitoBean private TransactionRepository transactionRepository;

    private MockMvc mockMvc;
    private final List<Customer> customers = new ArrayList<>();
    private final AtomicLong ids = new AtomicLong();

    @BeforeEach
    void setUp() {
        customers.clear();
        ids.set(0);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        when(customerRepository.existsByCpf(any())).thenAnswer(invocation ->
                customers.stream().anyMatch(customer -> customer.getCpf().equals(invocation.getArgument(0))));
        when(customerRepository.existsByEmail(any())).thenAnswer(invocation ->
                customers.stream().anyMatch(customer -> customer.getEmail().equals(invocation.getArgument(0))));
        when(customerRepository.saveAndFlush(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            ReflectionTestUtils.setField(customer, "id", ids.incrementAndGet());
            customers.add(customer);
            return customer;
        });
        when(customerRepository.findByCpf(any())).thenAnswer(invocation -> customers.stream()
                .filter(customer -> customer.getCpf().equals(invocation.getArgument(0)))
                .findFirst());
    }

    @Test
    void shouldCreateIndependentGuestsAndAuthenticateEachOne() throws Exception {
        MvcResult first = createGuest();
        MvcResult second = createGuest();
        String firstToken = token(first);
        String secondToken = token(second);

        assertThat(customers).hasSize(2);
        assertThat(customers).extracting(Customer::getId).doesNotHaveDuplicates();
        assertThat(customers).extracting(Customer::getCpf).doesNotHaveDuplicates();
        assertThat(customers).extracting(Customer::getEmail).doesNotHaveDuplicates();
        assertThat(firstToken).isNotBlank().isNotEqualTo(secondToken);
        for (Customer customer : customers) {
            assertThat(customer.getName()).isEqualTo("Usuário Visitante");
            assertThat(customer.getEmail()).matches("guest-[0-9a-f-]{36}@spendly\\.demo");
            assertThat(customer.getPasswordHash()).startsWith("$2");
            assertThat(customer.getPasswordHash()).doesNotContain("guest-");
            assertThat(customer.getWallets()).isNull();
        }

        assertCurrentCustomer(firstToken, customers.get(0));
        assertCurrentCustomer(secondToken, customers.get(1));
        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldKeepOtherRoutesProtected() throws Exception {
        mockMvc.perform(get("/customers/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/wallets")).andExpect(status().isUnauthorized());
    }

    private MvcResult createGuest() throws Exception {
        return mockMvc.perform(post("/auth/guest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.cpf").doesNotExist())
                .andReturn();
    }

    private String token(MvcResult result) throws Exception {
        return com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private void assertCurrentCustomer(String token, Customer expected) throws Exception {
        mockMvc.perform(get("/customers/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.email").value(expected.getEmail()))
                .andExpect(jsonPath("$.cpf").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }
}
