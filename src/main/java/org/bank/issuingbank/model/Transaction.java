package org.bank.issuingbank.model;

import jakarta.persistence.*;
import org.bank.issuingbank.enums.TransactionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String merchantId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private String responseCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // ===== Constructors =====

    public Transaction() {
    }

    public Transaction(String merchantId,
                       Double amount,
                       String currency,
                       TransactionStatus status,
                       String responseCode,
                       LocalDateTime createdAt) {
        this.merchantId = merchantId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.responseCode = responseCode;
        this.createdAt = createdAt;
    }

    // ===== Getters & Setters =====

    public String getId() {
        return id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
