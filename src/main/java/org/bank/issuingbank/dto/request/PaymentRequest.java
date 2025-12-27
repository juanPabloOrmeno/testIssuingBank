package org.bank.issuingbank.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(

        @NotBlank(message = "MerchantId is required")
        String merchantId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        Double amount,

        @NotBlank(message = "Currency is required")
        String currency,

        @NotBlank(message = "Card token is required")
        String cardToken,

        @NotBlank(message = "Expiration date is required")
        String expirationDate
) {}
