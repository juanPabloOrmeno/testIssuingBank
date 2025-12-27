package org.bank.issuingbank.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentRequest {

    @NotBlank(message = "MerchantId is required")
    private String merchantId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Card token is required")
    private String cardToken;

    @NotBlank(message = "Expiration date is required")
    private String expirationDate;

    // Constructor
    public PaymentRequest() {
    }

    public PaymentRequest(String merchantId, Double amount, String currency, String cardToken, String expirationDate) {
        this.merchantId = merchantId;
        this.amount = amount;
        this.currency = currency;
        this.cardToken = cardToken;
        this.expirationDate = expirationDate;
    }

    // Getters
    public String getMerchantId() {
        return merchantId;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCardToken() {
        return cardToken;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    // Setters
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
