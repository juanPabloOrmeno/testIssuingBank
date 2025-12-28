package org.bank.issuingbank.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.bank.issuingbank.enums.TransactionStatus;

import java.time.LocalDateTime;

@Schema(description = "Respuesta de procesamiento de pago")
public record PaymentResponse(
        @Schema(description = "ID único de la transacción", example = "123e4567-e89b-12d3-a456-426614174000")
        String transactionId,
        
        @Schema(description = "Estado de la transacción", example = "APPROVED")
        TransactionStatus status,
        
        @Schema(description = "Código de respuesta", example = "00")
        String responseCode,
        
        @Schema(description = "Fecha de creación de la transacción", example = "2025-12-28T10:30:00")
        LocalDateTime createdAt
) {}
