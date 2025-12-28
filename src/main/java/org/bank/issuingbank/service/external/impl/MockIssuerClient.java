package org.bank.issuingbank.service.external.impl;

import org.bank.issuingbank.exception.BusinessException;
import org.bank.issuingbank.service.external.IssuerClient;
import org.bank.issuingbank.service.external.dto.IssuerResponse;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.regex.Pattern;

@Component
public class MockIssuerClient implements IssuerClient {

    private static final double MAX_AMOUNT = 1000000;
    private static final int MIN_TOKEN_LENGTH = 10;
    private static final Pattern TOKEN_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");
    private static final String BLOCKED_CARD_SUFFIX = "999";
    private static final String INVALID_CARD_PATTERN = "0000";
    
    private final Random random = new Random();

    @Override
    public IssuerResponse authorize(String cardToken, Double amount, String currency) {

        // Validación 1: Token no nulo ni vacío
        if (cardToken == null || cardToken.trim().isEmpty()) {
            throw new BusinessException("Card token cannot be null or empty");
        }

        // Validación 2: Token debe tener al menos 10 caracteres
        if (cardToken.length() < MIN_TOKEN_LENGTH) {
            throw new BusinessException("Card token must be at least " + MIN_TOKEN_LENGTH + " characters long");
        }

        // Validación 3: Token solo puede contener letras, números y guiones bajos
        if (!TOKEN_PATTERN.matcher(cardToken).matches()) {
            throw new BusinessException("Card token can only contain letters, numbers, and underscores");
        }

        // Validación 4: Token NO puede terminar en "999" (tarjeta bloqueada)
        if (cardToken.endsWith(BLOCKED_CARD_SUFFIX)) {
            return new IssuerResponse(false, "CARD_BLOCKED");
        }

        // Validación 5: Token NO puede contener "0000" (tarjeta inválida)
        if (cardToken.contains(INVALID_CARD_PATTERN)) {
            return new IssuerResponse(false, "INVALID_CARD");
        }

        // Validación 6: Monto debe ser mayor a 0
        if (amount <= 0) {
            throw new BusinessException("Transaction amount must be greater than zero");
        }

        // Validación 7: Monto no puede superar el límite máximo
        if (amount > MAX_AMOUNT) {
            return new IssuerResponse(false, "LIMIT_EXCEEDED");
        }

        // Simula respuesta aleatoria del banco
        boolean approved = random.nextBoolean();

        return new IssuerResponse(
                approved,
                approved ? "00" : "05"
        );
    }
}
