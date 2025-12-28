package org.bank.issuingbank.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.bank.issuingbank.dto.request.PaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PaymentRequest Validation Tests")
class PaymentRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debe validar un PaymentRequest válido sin errores")
    void shouldValidateValidPaymentRequestWithoutErrors() {
        // Given
        PaymentRequest request = new PaymentRequest(
                "MERCHANT_001",
                50000.0,
                "CLP",
                "tok_abc123xyz",
                "12/26"
        );

        // When
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Debe fallar validación cuando merchantId es null")
    void shouldFailValidationWhenMerchantIdIsNull() {
        // Given
        PaymentRequest request = new PaymentRequest(
                null,
                50000.0,
                "CLP",
                "tok_abc123xyz",
                "12/26"
        );

        // When
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("MerchantId is required");
    }

    @Test
    @DisplayName("Debe fallar validación cuando merchantId está vacío")
    void shouldFailValidationWhenMerchantIdIsEmpty() {
        // Given
        PaymentRequest request = new PaymentRequest(
                "",
                50000.0,
                "CLP",
                "tok_abc123xyz",
                "12/26"
        );

        // When
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("MerchantId is required");
    }

    @Test
    @DisplayName("Debe fallar validación cuando amount es null")
    void shouldFailValidationWhenAmountIsNull() {
        // Given
        PaymentRequest request = new PaymentRequest(
                "MERCHANT_001",
                null,
                "CLP",
                "tok_abc123xyz",
                "12/26"
        );

        // When
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Amount is required");
    }

    @Test
    @DisplayName("Debe fallar validación cuando amount es cero")
    void shouldFailValidationWhenAmountIsZero() {
        // Given
        PaymentRequest request = new PaymentRequest(
                "MERCHANT_001",
                0.0,
                "CLP",
                "tok_abc123xyz",
                "12/26"
        );

        // When
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Amount must be greater than zero");
    }

    @Test
    @DisplayName("Debe fallar validación cuando amount es negativo")
    void shouldFailValidationWhenAmountIsNegative() {
        // Given
        PaymentRequest request = new PaymentRequest(
                "MERCHANT_001",
                -1000.0,
                "CLP",
                "tok_abc123xyz",
                "12/26"
        );

        // When
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Amount must be greater than zero");
    }

    @Test
    @DisplayName("Debe fallar validación cuando currency es null")
    void shouldFailValidationWhenCurrencyIsNull() {
        // Given
        PaymentRequest request = new PaymentRequest(
                "MERCHANT_001",
                50000.0,
                null,
                "tok_abc123xyz",
                "12/26"
        );

        // When
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Currency is required");
    }

    @Test
    @DisplayName("Debe fallar validación cuando cardToken es null")
    void shouldFailValidationWhenCardTokenIsNull() {
        // Given
        PaymentRequest request = new PaymentRequest(
                "MERCHANT_001",
                50000.0,
                "CLP",
                null,
                "12/26"
        );

        // When
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Card token is required");
    }

    @Test
    @DisplayName("Debe fallar validación cuando expirationDate es null")
    void shouldFailValidationWhenExpirationDateIsNull() {
        // Given
        PaymentRequest request = new PaymentRequest(
                "MERCHANT_001",
                50000.0,
                "CLP",
                "tok_abc123xyz",
                null
        );

        // When
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Expiration date is required");
    }

    @Test
    @DisplayName("Debe tener múltiples violaciones cuando varios campos son inválidos")
    void shouldHaveMultipleViolationsWhenMultipleFieldsAreInvalid() {
        // Given
        PaymentRequest request = new PaymentRequest(
                "",
                -100.0,
                "",
                "",
                ""
        );

        // When
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(5);
    }

    @Test
    @DisplayName("Debe aceptar diferentes formatos de expirationDate")
    void shouldAcceptDifferentExpirationDateFormats() {
        // Given
        String[] validFormats = {"12/26", "01/25", "06/30"};

        for (String format : validFormats) {
            PaymentRequest request = new PaymentRequest(
                    "MERCHANT_001",
                    50000.0,
                    "CLP",
                    "tok_abc123xyz",
                    format
            );

            // When
            Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Test
    @DisplayName("Debe aceptar diferentes monedas válidas")
    void shouldAcceptDifferentValidCurrencies() {
        // Given
        String[] currencies = {"CLP", "USD", "EUR", "BRL", "MXN"};

        for (String currency : currencies) {
            PaymentRequest request = new PaymentRequest(
                    "MERCHANT_001",
                    1000.0,
                    currency,
                    "tok_abc123xyz",
                    "12/26"
            );

            // When
            Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }
}
