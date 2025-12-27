package org.bank.issuingbank.dto.response;

import org.bank.issuingbank.enums.TransactionStatus;

import java.time.LocalDateTime;

public class PaymentResponse {

    private String transactionId;
    private TransactionStatus status;
    private String responseCode;
    private LocalDateTime createdAt;

    public PaymentResponse(
            String transactionId,
            TransactionStatus status,
            String responseCode,
            LocalDateTime createdAt
    ) {
        this.transactionId = transactionId;
        this.status = status;
        this.responseCode = responseCode;
        this.createdAt = createdAt;
    }

    public String getTransactionId() {
        return transactionId;
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
}
