package com.spendly.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Setter
    @Column(nullable = false, length = 50)
    private String name;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @NotNull
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletType walletType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus status;

    @NotNull
    @Column(nullable = false)
    private LocalDate openingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public Wallet(String name, WalletType walletType, java.math.BigDecimal initialBalance, Customer customer) {
        this.name = name;
        this.walletType = walletType;
        this.customer = customer;
        this.balance = initialBalance != null ? initialBalance : BigDecimal.ZERO;
        this.status = WalletStatus.ACTIVE;
        this.openingDate = LocalDate.now();
    }

    public void deactivate() {
        this.status = WalletStatus.INACTIVE;
    }

    public void activate() {
        this.status = WalletStatus.ACTIVE;
    }

    public void increaseBalance(java.math.BigDecimal amount) {
        if (amount == null) return;
        this.balance = this.balance.add(amount);
    }

    public void decreaseBalance(java.math.BigDecimal amount) {
        if (amount == null) return;
        this.balance = this.balance.subtract(amount);
    }
}
