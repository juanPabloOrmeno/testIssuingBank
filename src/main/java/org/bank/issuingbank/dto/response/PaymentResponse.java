package org.bank.issuingbank.dto.response;

import org.bank.issuingbank.enums.TransactionStatus;

import java.time.LocalDateTime;

public record PaymentResponse(
        String transactionId,
        TransactionStatus status,
        String responseCode,
        LocalDateTime createdAt
) {}
