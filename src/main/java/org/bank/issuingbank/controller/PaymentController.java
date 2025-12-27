package org.bank.issuingbank.controller;

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
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Procesa un pago recibido desde un comercio
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request
    ) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Obtiene el estado de una transacción por ID
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable @NotBlank(message = "Transaction ID is required") String transactionId
    ) {
        PaymentResponse response = paymentService.getPaymentById(transactionId);
        return ResponseEntity.ok(response);
    }
}
