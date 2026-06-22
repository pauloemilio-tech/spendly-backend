package com.spendly.domain.service;

import com.spendly.api.dto.request.WalletRequestDTO;
import com.spendly.api.dto.request.WalletUpdateRequestDTO;
import com.spendly.api.dto.response.WalletResponseDTO;
import com.spendly.domain.entity.Customer;
import com.spendly.domain.entity.Wallet;
import com.spendly.domain.entity.WalletStatus;
import com.spendly.domain.entity.WalletType;
import com.spendly.domain.exception.WalletNotFoundException;
import com.spendly.domain.repository.CustomerRepository;
import com.spendly.domain.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    private static final String CPF = "12345678901";
    private static final Long CUSTOMER_ID = 10L;
    private static final Long WALLET_ID = 20L;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void shouldCreateActiveWalletForAuthenticatedCustomerWithRequestData() {
        Customer customer = new Customer("Cliente", CPF, "hash", "cliente@spendly.com");
        when(customerRepository.findByCpf(CPF)).thenReturn(Optional.of(customer));
        WalletRequestDTO request = new WalletRequestDTO(
                "Principal",
                WalletType.BANK_ACCOUNT,
                new BigDecimal("150.50")
        );
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);

        WalletResponseDTO response = walletService.createWallet(CPF, request);

        verify(walletRepository).save(walletCaptor.capture());
        Wallet savedWallet = walletCaptor.getValue();
        assertThat(savedWallet.getName()).isEqualTo("Principal");
        assertThat(savedWallet.getWalletType()).isEqualTo(WalletType.BANK_ACCOUNT);
        assertThat(savedWallet.getBalance()).isEqualByComparingTo("150.50");
        assertThat(savedWallet.getStatus()).isEqualTo(WalletStatus.ACTIVE);
        assertThat(savedWallet.getCustomer()).isSameAs(customer);
        assertThat(response.name()).isEqualTo("Principal");
        assertThat(response.balance()).isEqualByComparingTo("150.50");
        assertThat(response.status()).isEqualTo(WalletStatus.ACTIVE.name());
    }

    @Test
    void shouldListActiveWalletsBelongingToAuthenticatedCustomer() {
        stubCustomer();
        Customer owner = new Customer("Cliente", CPF, "hash", "cliente@spendly.com");
        Wallet bankAccount = new Wallet(
                "Principal",
                WalletType.BANK_ACCOUNT,
                new BigDecimal("100.00"),
                owner
        );
        Wallet cash = new Wallet(
                "Dinheiro",
                WalletType.CASH,
                new BigDecimal("25.50"),
                owner
        );
        when(walletRepository.findAllByCustomerIdAndStatus(CUSTOMER_ID, WalletStatus.ACTIVE))
                .thenReturn(List.of(bankAccount, cash));

        List<WalletResponseDTO> response = walletService.listWallets(CPF);

        assertThat(response)
                .extracting(WalletResponseDTO::name)
                .containsExactly("Principal", "Dinheiro");
        verify(walletRepository).findAllByCustomerIdAndStatus(CUSTOMER_ID, WalletStatus.ACTIVE);
    }

    @Test
    void shouldUpdateActiveWalletBelongingToAuthenticatedCustomer() {
        Wallet wallet = wallet("Principal", WalletType.BANK_ACCOUNT, "100.00");
        stubCustomerAndActiveWallet(wallet);
        WalletUpdateRequestDTO request = new WalletUpdateRequestDTO("Reserva", WalletType.INVESTMENT);
        when(walletRepository.save(wallet)).thenReturn(wallet);

        WalletResponseDTO response = walletService.updateWallet(CPF, WALLET_ID, request);

        assertThat(wallet.getName()).isEqualTo("Reserva");
        assertThat(wallet.getWalletType()).isEqualTo(WalletType.INVESTMENT);
        assertThat(response.name()).isEqualTo("Reserva");
        assertThat(response.walletType()).isEqualTo(WalletType.INVESTMENT.name());
        verify(walletRepository).save(wallet);
    }

    @Test
    void shouldRejectUpdateWhenNoActiveWalletBelongingToAuthenticatedCustomerIsFound() {
        stubCustomer();
        when(walletRepository.findByIdAndCustomerIdAndStatus(WALLET_ID, CUSTOMER_ID, WalletStatus.ACTIVE))
                .thenReturn(Optional.empty());
        WalletUpdateRequestDTO request = new WalletUpdateRequestDTO("Reserva", WalletType.INVESTMENT);

        assertThrows(WalletNotFoundException.class,
                () -> walletService.updateWallet(CPF, WALLET_ID, request));

        verify(walletRepository).findByIdAndCustomerIdAndStatus(
                WALLET_ID,
                CUSTOMER_ID,
                WalletStatus.ACTIVE
        );
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void shouldDeactivateWalletBelongingToAuthenticatedCustomerBySoftDelete() {
        Wallet wallet = wallet("Principal", WalletType.BANK_ACCOUNT, "100.00");
        stubCustomer();
        when(walletRepository.findByIdAndCustomerId(WALLET_ID, CUSTOMER_ID)).thenReturn(Optional.of(wallet));

        walletService.deactivateWallet(CPF, WALLET_ID);

        assertThat(wallet.getStatus()).isEqualTo(WalletStatus.INACTIVE);
        verify(walletRepository).save(wallet);
    }

    @Test
    void shouldNotPersistDeactivationWhenNoWalletBelongingToAuthenticatedCustomerIsFound() {
        stubCustomer();
        when(walletRepository.findByIdAndCustomerId(WALLET_ID, CUSTOMER_ID)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class,
                () -> walletService.deactivateWallet(CPF, WALLET_ID));

        verify(walletRepository).findByIdAndCustomerId(WALLET_ID, CUSTOMER_ID);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    private void stubCustomerAndActiveWallet(Wallet wallet) {
        stubCustomer();
        when(walletRepository.findByIdAndCustomerIdAndStatus(WALLET_ID, CUSTOMER_ID, WalletStatus.ACTIVE))
                .thenReturn(Optional.of(wallet));
    }

    private Customer stubCustomer() {
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(CUSTOMER_ID);
        when(customerRepository.findByCpf(CPF)).thenReturn(Optional.of(customer));
        return customer;
    }

    private Wallet wallet(String name, WalletType type, String balance) {
        Customer owner = new Customer("Cliente", CPF, "hash", "cliente@spendly.com");
        return new Wallet(name, type, new BigDecimal(balance), owner);
    }
}
