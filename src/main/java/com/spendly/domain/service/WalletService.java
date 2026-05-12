package com.spendly.domain.service;

import com.spendly.api.dto.request.WalletRequestDTO;
import com.spendly.api.dto.request.WalletUpdateRequestDTO;
import com.spendly.api.dto.response.WalletResponseDTO;
import com.spendly.domain.entity.Customer;
import com.spendly.domain.entity.Wallet;
import com.spendly.domain.exception.WalletNotFoundException;
import com.spendly.domain.repository.CustomerRepository;
import com.spendly.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;

    public WalletService(WalletRepository walletRepository, CustomerRepository customerRepository) {
        this.walletRepository = walletRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public WalletResponseDTO createWallet(String cpf, WalletRequestDTO dto) {
        Customer customer = resolveCustomer(cpf);
        Wallet wallet = new Wallet(dto.name(), dto.walletType(), customer);
        return WalletResponseDTO.from(walletRepository.save(wallet));
    }

    public List<WalletResponseDTO> listWallets(String cpf) {
        Customer customer = resolveCustomer(cpf);
        return walletRepository.findAllByCustomerId(customer.getId())
                .stream()
                .map(WalletResponseDTO::from)
                .toList();
    }

    public WalletResponseDTO getWallet(String cpf, Long walletId) {
        Customer customer = resolveCustomer(cpf);
        Wallet wallet = findOwnedWallet(walletId, customer.getId());
        return WalletResponseDTO.from(wallet);
    }

    @Transactional
    public WalletResponseDTO updateWallet(String cpf, Long walletId, WalletUpdateRequestDTO dto) {
        Customer customer = resolveCustomer(cpf);
        Wallet wallet = findOwnedWallet(walletId, customer.getId());

        if (dto.name() != null) {
            wallet.setName(dto.name());
        }
        if (dto.walletType() != null) {
            wallet.setWalletType(dto.walletType());
        }

        return WalletResponseDTO.from(walletRepository.save(wallet));
    }

    @Transactional
    public void deactivateWallet(String cpf, Long walletId) {
        Customer customer = resolveCustomer(cpf);
        Wallet wallet = findOwnedWallet(walletId, customer.getId());
        wallet.deactivate();
        walletRepository.save(wallet);
    }

    // --- helpers privados ---

    private Customer resolveCustomer(String cpf) {
        return customerRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Cliente autenticado não encontrado"));
    }

    private Wallet findOwnedWallet(Long walletId, Long customerId) {
        return walletRepository.findByIdAndCustomerId(walletId, customerId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
    }
}
