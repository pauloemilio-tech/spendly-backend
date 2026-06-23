package com.spendly.domain.repository;

import com.spendly.domain.entity.Customer;
import com.spendly.domain.entity.Transaction;
import com.spendly.domain.entity.TransactionCategory;
import com.spendly.domain.entity.TransactionType;
import com.spendly.domain.entity.Wallet;
import com.spendly.domain.entity.WalletStatus;
import com.spendly.domain.entity.WalletType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("integration-test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
class RepositoryIntegrationTest {

    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine")
            .withDatabaseName("spendly_test")
            .withUsername("spendly_test")
            .withPassword("spendly_test");

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldRestrictWalletQueriesAndActiveAggregatesToRequestedCustomer() {
        Customer customer = saveCustomer("12345678901", "cliente@spendly.com");
        Customer otherCustomer = saveCustomer("10987654321", "outro@spendly.com");
        Wallet checking = saveWallet("Principal", "100.00", customer);
        saveWallet("Dinheiro", "50.50", customer);
        Wallet inactive = saveWallet("Antiga", "900.00", customer);
        inactive.deactivate();
        walletRepository.save(inactive);
        Wallet otherWallet = saveWallet("Outro cliente", "700.00", otherCustomer);

        List<Wallet> ownedWallets = walletRepository.findAllByCustomerId(customer.getId());
        List<Wallet> activeWallets = walletRepository.findAllByCustomerIdAndStatus(
                customer.getId(),
                WalletStatus.ACTIVE
        );

        assertThat(ownedWallets)
                .extracting(Wallet::getName)
                .containsExactlyInAnyOrder("Principal", "Dinheiro", "Antiga");
        assertThat(activeWallets)
                .extracting(Wallet::getName)
                .containsExactlyInAnyOrder("Principal", "Dinheiro");
        assertThat(walletRepository.countByCustomerIdAndStatus(customer.getId(), WalletStatus.ACTIVE))
                .isEqualTo(2L);
        assertThat(walletRepository.sumBalanceByCustomerIdAndStatus(customer.getId(), WalletStatus.ACTIVE))
                .isEqualByComparingTo("150.50");
        assertThat(walletRepository.findByIdAndCustomerId(checking.getId(), customer.getId()))
                .contains(checking);
        assertThat(walletRepository.findByIdAndCustomerId(checking.getId(), otherCustomer.getId()))
                .isEmpty();
        assertThat(walletRepository.findByIdAndCustomerIdAndStatus(
                inactive.getId(),
                customer.getId(),
                WalletStatus.ACTIVE
        )).isEmpty();
        assertThat(activeWallets).doesNotContain(otherWallet);
        assertThat(activeWallets).doesNotContain(inactive);
    }

    @Test
    void shouldAggregateAndCountTransactionsOnlyForRequestedCustomer() {
        Customer customer = saveCustomer("12345678901", "cliente@spendly.com");
        Customer otherCustomer = saveCustomer("10987654321", "outro@spendly.com");
        Wallet wallet = saveWallet("Principal", "0.00", customer);
        Wallet otherWallet = saveWallet("Outra", "0.00", otherCustomer);
        saveTransaction(wallet, TransactionType.INCOME, TransactionCategory.SALARY, "1000.00", "Salário");
        saveTransaction(wallet, TransactionType.INCOME, TransactionCategory.FREELANCE, "250.50", "Projeto");
        saveTransaction(wallet, TransactionType.EXPENSE, TransactionCategory.FOOD, "80.25", "Mercado");
        saveTransaction(otherWallet, TransactionType.INCOME, TransactionCategory.SALARY, "9000.00", "Outro salário");
        saveTransaction(otherWallet, TransactionType.EXPENSE, TransactionCategory.BILLS, "4000.00", "Outra conta");

        List<Transaction> transactions = transactionRepository.findAllByWalletCustomerId(customer.getId());

        assertThat(transactions)
                .extracting(Transaction::getDescription)
                .containsExactlyInAnyOrder("Salário", "Projeto", "Mercado");
        assertThat(transactionRepository.sumAmountByCustomerIdAndType(customer.getId(), TransactionType.INCOME))
                .isEqualByComparingTo("1250.50");
        assertThat(transactionRepository.sumAmountByCustomerIdAndType(customer.getId(), TransactionType.EXPENSE))
                .isEqualByComparingTo("80.25");
        assertThat(transactionRepository.countByWalletCustomerId(customer.getId())).isEqualTo(3L);
    }

    @Test
    void shouldReturnFiveMostRecentTransactionsInDescendingCreationOrderForRequestedCustomer() {
        Customer customer = saveCustomer("12345678901", "cliente@spendly.com");
        Customer otherCustomer = saveCustomer("10987654321", "outro@spendly.com");
        Wallet wallet = saveWallet("Principal", "0.00", customer);
        Wallet otherWallet = saveWallet("Outra", "0.00", otherCustomer);
        List<Transaction> transactions = new ArrayList<>();

        for (int index = 1; index <= 7; index++) {
            transactions.add(saveTransaction(
                    wallet,
                    TransactionType.EXPENSE,
                    TransactionCategory.FOOD,
                    Integer.toString(index) + ".00",
                    "Transação " + index
            ));
        }
        Transaction otherTransaction = saveTransaction(
                otherWallet,
                TransactionType.INCOME,
                TransactionCategory.SALARY,
                "9999.00",
                "Transação de outro cliente"
        );
        transactionRepository.flush();

        LocalDateTime baseTime = LocalDateTime.of(2026, 1, 1, 12, 0);
        for (int index = 0; index < transactions.size(); index++) {
            updateCreatedAt(transactions.get(index), baseTime.plusDays(index));
        }
        updateCreatedAt(otherTransaction, baseTime.plusYears(1));
        entityManager.clear();

        List<Transaction> recentTransactions =
                transactionRepository.findTop5ByWalletCustomerIdOrderByCreatedAtDesc(customer.getId());

        assertThat(recentTransactions)
                .extracting(Transaction::getDescription)
                .containsExactly(
                        "Transação 7",
                        "Transação 6",
                        "Transação 5",
                        "Transação 4",
                        "Transação 3"
                );
        assertThat(recentTransactions)
                .extracting(Transaction::getCreatedAt)
                .isSortedAccordingTo((left, right) -> right.compareTo(left));
        assertThat(recentTransactions)
                .extracting(transaction -> transaction.getWallet().getCustomer().getId())
                .containsOnly(customer.getId());
    }

    private Customer saveCustomer(String cpf, String email) {
        return customerRepository.save(new Customer("Cliente", cpf, "hash", email));
    }

    private Wallet saveWallet(String name, String balance, Customer customer) {
        return walletRepository.save(new Wallet(
                name,
                WalletType.BANK_ACCOUNT,
                new BigDecimal(balance),
                customer
        ));
    }

    private Transaction saveTransaction(Wallet wallet,
                                        TransactionType type,
                                        TransactionCategory category,
                                        String amount,
                                        String description) {
        return transactionRepository.save(new Transaction(
                wallet,
                type,
                category,
                new BigDecimal(amount),
                description
        ));
    }

    private void updateCreatedAt(Transaction transaction, LocalDateTime createdAt) {
        entityManager.createQuery("update Transaction t set t.createdAt = :createdAt where t.id = :id")
                .setParameter("createdAt", createdAt)
                .setParameter("id", transaction.getId())
                .executeUpdate();
    }
}
