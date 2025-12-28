package org.bank.issuingbank.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Solicitud de pago")
public record PaymentRequest(
    @Schema(description = "ID del comercio", example = "MERCHANT_001")
    @NotBlank(message = "MerchantId is required")
    String merchantId,

    @Schema(description = "Monto de la transacción", example = "5000.0")
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    Double amount,

    @Schema(description = "Código de moneda", example = "CLP")
    @NotBlank(message = "Currency is required")
    String currency,

    @Schema(description = "Token de la tarjeta", example = "tok_1234567890")
    @NotBlank(message = "Card token is required")
    String cardToken,

    @Schema(description = "Fecha de expiración de la tarjeta", example = "12/26")
    @NotBlank(message = "Expiration date is required")
    String expirationDate
) {
}
