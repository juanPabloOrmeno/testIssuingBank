package org.bank.issuingbank.service.external.impl;

import org.bank.issuingbank.service.external.IssuerClient;
import org.bank.issuingbank.service.external.dto.IssuerResponse;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MockIssuerClient implements IssuerClient {

    private static final double MAX_AMOUNT = 1000000;
    private final Random random = new Random();

    @Override
    public IssuerResponse authorize(String cardToken, Double amount, String currency) {

        // Simula rechazo por monto alto
        if (amount > MAX_AMOUNT) {
            return new IssuerResponse(false, "LIMIT_EXCEEDED");
        }

        // Simula tarjeta bloqueada
        if (cardToken.endsWith("999")) {
            return new IssuerResponse(false, "CARD_BLOCKED");
        }

        // Simula respuesta aleatoria del banco
        boolean approved = random.nextBoolean();

        return new IssuerResponse(
                approved,
                approved ? "00" : "05"
        );
    }
}
