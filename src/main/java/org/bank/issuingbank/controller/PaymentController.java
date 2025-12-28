package org.bank.issuingbank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.bank.issuingbank.dto.request.PaymentRequest;
import org.bank.issuingbank.dto.response.PaymentResponse;
import org.bank.issuingbank.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments", description = "API de procesamiento de pagos del banco emisor")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Procesa un pago recibido desde un comercio
     */
    @PostMapping
    @Operation(
            summary = "Procesar un nuevo pago",
            description = "Autoriza y procesa una transacción de pago recibida desde un comercio o acquirer"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pago procesado exitosamente",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida - validación fallida",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    public ResponseEntity<PaymentResponse> processPayment(
            @Parameter(description = "Datos de la solicitud de pago", required = true)
            @Valid @RequestBody PaymentRequest request
    ) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Obtiene el estado de una transacción por ID
     */
    @GetMapping("/{transactionId}")
    @Operation(
            summary = "Consultar estado de transacción",
            description = "Obtiene el estado actual de una transacción mediante su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transacción encontrada",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transacción no encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID de transacción inválido",
                    content = @Content
            )
    })
    public ResponseEntity<PaymentResponse> getPaymentById(
            @Parameter(description = "ID único de la transacción", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable @NotBlank(message = "Transaction ID is required") String transactionId
    ) {
        PaymentResponse response = paymentService.getPaymentById(transactionId);
        return ResponseEntity.ok(response);
    }
}
