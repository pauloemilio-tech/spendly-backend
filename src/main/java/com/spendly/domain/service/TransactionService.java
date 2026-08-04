package com.spendly.domain.service;

import com.spendly.api.dto.request.TransactionRequestDTO;
import com.spendly.api.dto.response.TransactionResponseDTO;
import com.spendly.domain.entity.Customer;
import com.spendly.domain.entity.Transaction;
import com.spendly.domain.entity.TransactionStatus;
import com.spendly.domain.entity.TransactionType;
import com.spendly.domain.entity.TransactionCategory;
import com.spendly.domain.entity.Wallet;
import com.spendly.domain.entity.WalletStatus;
import com.spendly.domain.exception.InsufficientFundsException;
import com.spendly.domain.exception.TransactionCategoryMismatchException;
import com.spendly.domain.exception.TransactionAlreadyReversedException;
import com.spendly.domain.exception.TransactionNotFoundException;
import com.spendly.domain.exception.WalletNotFoundException;
import com.spendly.domain.repository.CustomerRepository;
import com.spendly.domain.repository.TransactionRepository;
import com.spendly.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              WalletRepository walletRepository,
                              CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public TransactionResponseDTO createTransaction(String cpf, TransactionRequestDTO dto) {
        Customer customer = resolveCustomer(cpf);
        Wallet wallet = walletRepository.findByIdAndCustomerIdAndStatus(dto.walletId(), customer.getId(), WalletStatus.ACTIVE)
            .orElseThrow(() -> new WalletNotFoundException(dto.walletId()));

        // category/type compatibility validation
        if (!dto.category().isCompatibleWith(dto.type())) {
            throw new TransactionCategoryMismatchException(dto.type(), dto.category());
        }

        // business validations
        if (dto.amount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero");
        }

        if (dto.type() == TransactionType.EXPENSE) {
            if (wallet.getBalance().compareTo(dto.amount()) < 0) {
                throw new InsufficientFundsException();
            }
            wallet.decreaseBalance(dto.amount());
        } else if (dto.type() == TransactionType.INCOME) {
            wallet.increaseBalance(dto.amount());
        }

        // persist changes
        walletRepository.save(wallet);

        Transaction tx = new Transaction(wallet, dto.type(), dto.category(), dto.amount(), dto.description());
        Transaction saved = transactionRepository.save(tx);

        return TransactionResponseDTO.from(saved);
    }

    public List<TransactionResponseDTO> listTransactions(String cpf) {
        Customer customer = resolveCustomer(cpf);
        return transactionRepository.findAllByWalletCustomerId(customer.getId())
                .stream()
                .map(TransactionResponseDTO::from)
                .toList();
    }

    public TransactionResponseDTO getTransaction(String cpf, Long id) {
        Customer customer = resolveCustomer(cpf);
        Transaction tx = transactionRepository.findByIdAndWalletCustomerId(id, customer.getId())
                .orElseThrow(() -> new TransactionNotFoundException(id));
        return TransactionResponseDTO.from(tx);
    }

    @Transactional
    public TransactionResponseDTO reverseTransaction(String cpf, Long transactionId) {
        Customer customer = resolveCustomer(cpf);
        Transaction transaction = transactionRepository
                .findByIdAndWalletCustomerId(transactionId, customer.getId())
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        if (transaction.getStatus() == TransactionStatus.REVERSED) {
            throw new TransactionAlreadyReversedException(transactionId);
        }

        Wallet wallet = transaction.getWallet();
        if (transaction.getType() == TransactionType.INCOME) {
            if (wallet.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new InsufficientFundsException(
                        "Insufficient wallet balance to reverse income transaction"
                );
            }
            wallet.decreaseBalance(transaction.getAmount());
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            wallet.increaseBalance(transaction.getAmount());
        }

        transaction.reverse();
        walletRepository.save(wallet);
        Transaction saved = transactionRepository.save(transaction);

        return TransactionResponseDTO.from(saved);
    }

    // --- helpers ---
    private Customer resolveCustomer(String cpf) {
        return customerRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Cliente autenticado não encontrado"));
    }
}
