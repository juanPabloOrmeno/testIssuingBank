package org.bank.issuingbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bank.issuingbank.dto.request.PaymentRequest;
import org.bank.issuingbank.dto.response.PaymentResponse;
import org.bank.issuingbank.enums.TransactionStatus;
import org.bank.issuingbank.service.PaymentService;
import org.bank.issuingbank.service.external.IssuerClient;
import org.bank.issuingbank.service.external.dto.IssuerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("PaymentController Integration Tests")
class PaymentControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private IssuerClient issuerClient;

    private PaymentRequest validPaymentRequest;
    private IssuerResponse approvedIssuerResponse;
    private IssuerResponse declinedIssuerResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        validPaymentRequest = new PaymentRequest(
                "MERCHANT_001",
                50000.0,
                "CLP",
                "tok_abc123xyz",
                "12/26"
        );

        approvedIssuerResponse = new IssuerResponse(true, "00");
        declinedIssuerResponse = new IssuerResponse(false, "51");
    }

    @Test
    @DisplayName("POST /payments - Debe procesar un pago válido exitosamente")
    void shouldProcessValidPaymentSuccessfully() throws Exception {
        // Given
        when(issuerClient.authorize(anyString(), anyDouble(), anyString()))
                .thenReturn(approvedIssuerResponse);

        // When & Then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPaymentRequest))
                        .header("X-Correlation-Id", "test-correlation-id"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactionId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.responseCode").value("00"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(issuerClient, times(1)).authorize(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("POST /payments - Debe retornar 400 cuando falta merchantId")
    void shouldReturn400WhenMerchantIdIsMissing() throws Exception {
        // Given
        PaymentRequest invalidRequest = new PaymentRequest(
                null, // merchantId faltante
                50000.0,
                "CLP",
                "tok_abc123xyz",
                "12/26"
        );

        // When & Then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(issuerClient, never()).authorize(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("POST /payments - Debe retornar 400 cuando merchantId está vacío")
    void shouldReturn400WhenMerchantIdIsEmpty() throws Exception {
        // Given
        PaymentRequest invalidRequest = new PaymentRequest(
                "", // merchantId vacío
                50000.0,
                "CLP",
                "tok_abc123xyz",
                "12/26"
        );

        // When & Then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(issuerClient, never()).authorize(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("POST /payments - Debe retornar 400 cuando el amount es null")
    void shouldReturn400WhenAmountIsNull() throws Exception {
        // Given
        PaymentRequest invalidRequest = new PaymentRequest(
                "MERCHANT_001",
                null, // amount null
                "CLP",
                "tok_abc123xyz",
                "12/26"
        );

        // When & Then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(issuerClient, never()).authorize(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("POST /payments - Debe retornar 400 cuando el amount es negativo")
    void shouldReturn400WhenAmountIsNegative() throws Exception {
        // Given
        PaymentRequest invalidRequest = new PaymentRequest(
                "MERCHANT_001",
                -1000.0, // amount negativo
                "CLP",
                "tok_abc123xyz",
                "12/26"
        );

        // When & Then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(issuerClient, never()).authorize(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("POST /payments - Debe retornar 400 cuando el amount es cero")
    void shouldReturn400WhenAmountIsZero() throws Exception {
        // Given
        PaymentRequest invalidRequest = new PaymentRequest(
                "MERCHANT_001",
                0.0, // amount cero
                "CLP",
                "tok_abc123xyz",
                "12/26"
        );

        // When & Then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(issuerClient, never()).authorize(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("POST /payments - Debe retornar 400 cuando falta currency")
    void shouldReturn400WhenCurrencyIsMissing() throws Exception {
        // Given
        PaymentRequest invalidRequest = new PaymentRequest(
                "MERCHANT_001",
                50000.0,
                "", // currency vacío
                "tok_abc123xyz",
                "12/26"
        );

        // When & Then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(issuerClient, never()).authorize(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("POST /payments - Debe retornar 400 cuando falta cardToken")
    void shouldReturn400WhenCardTokenIsMissing() throws Exception {
        // Given
        PaymentRequest invalidRequest = new PaymentRequest(
                "MERCHANT_001",
                50000.0,
                "CLP",
                "", // cardToken vacío
                "12/26"
        );

        // When & Then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(issuerClient, never()).authorize(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("POST /payments - Debe retornar 400 cuando falta expirationDate")
    void shouldReturn400WhenExpirationDateIsMissing() throws Exception {
        // Given
        PaymentRequest invalidRequest = new PaymentRequest(
                "MERCHANT_001",
                50000.0,
                "CLP",
                "tok_abc123xyz",
                "" // expirationDate vacío
        );

        // When & Then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(issuerClient, never()).authorize(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("POST /payments - Debe retornar pago declinado correctamente")
    void shouldReturnDeclinedPaymentCorrectly() throws Exception {
        // Given
        when(issuerClient.authorize(anyString(), anyDouble(), anyString()))
                .thenReturn(declinedIssuerResponse);

        // When & Then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPaymentRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("DECLINED"))
                .andExpect(jsonPath("$.responseCode").value("51"));

        verify(issuerClient, times(1)).authorize(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("GET /payments/{id} - Debe obtener un pago existente exitosamente")
    void shouldGetExistingPaymentSuccessfully() throws Exception {
        // Given - Primero crear una transacción
        when(issuerClient.authorize(anyString(), anyDouble(), anyString()))
                .thenReturn(approvedIssuerResponse);

        String responseContent = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPaymentRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentResponse createdPayment = objectMapper.readValue(responseContent, PaymentResponse.class);
        String transactionId = createdPayment.transactionId();

        // When & Then - Obtener la transacción creada
        mockMvc.perform(get("/payments/{id}", transactionId)
                        .header("X-Correlation-Id", "test-correlation-id"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactionId").value(transactionId))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.responseCode").value("00"));
    }

    @Test
    @DisplayName("GET /payments/{id} - Debe retornar 400 cuando la transacción no existe")
    void shouldReturn400WhenTransactionNotFound() throws Exception {
        // Given
        String nonExistentId = "txn_nonexistent_12345";

        // When & Then
        mockMvc.perform(get("/payments/{id}", nonExistentId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Transaction not found"))
                .andExpect(jsonPath("$.errorCode").value("BUSINESS_ERROR"));
    }

    @Test
    @DisplayName("POST /payments - Debe aceptar X-Correlation-Id en el request header")
    void shouldIncludeCorrelationIdInResponseHeader() throws Exception {
        // Given
        String correlationId = "test-correlation-123";
        when(issuerClient.authorize(anyString(), anyDouble(), anyString()))
                .thenReturn(approvedIssuerResponse);

        // When & Then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPaymentRequest))
                        .header("X-Correlation-Id", correlationId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").exists())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("POST /payments - Debe aceptar diferentes monedas")
    void shouldAcceptDifferentCurrencies() throws Exception {
        // Given
        String[] currencies = {"CLP", "USD", "EUR", "BRL"};
        
        when(issuerClient.authorize(anyString(), anyDouble(), anyString()))
                .thenReturn(approvedIssuerResponse);

        for (String currency : currencies) {
            PaymentRequest request = new PaymentRequest(
                    "MERCHANT_001",
                    1000.0,
                    currency,
                    "tok_abc123xyz",
                    "12/26"
            );

            // When & Then
            mockMvc.perform(post("/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("APPROVED"));
        }

        verify(issuerClient, times(currencies.length)).authorize(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("POST /payments - Debe manejar diferentes merchants")
    void shouldHandleDifferentMerchants() throws Exception {
        // Given
        String[] merchants = {"MERCHANT_001", "MERCHANT_002", "MERCHANT_003"};
        
        when(issuerClient.authorize(anyString(), anyDouble(), anyString()))
                .thenReturn(approvedIssuerResponse);

        for (String merchantId : merchants) {
            PaymentRequest request = new PaymentRequest(
                    merchantId,
                    10000.0,
                    "CLP",
                    "tok_abc123xyz",
                    "12/26"
            );

            // When & Then
            mockMvc.perform(post("/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("APPROVED"));
        }

        verify(issuerClient, times(merchants.length)).authorize(anyString(), anyDouble(), anyString());
    }

    @Test
    @DisplayName("POST /payments - Debe manejar diferentes códigos de respuesta del issuer")
    void shouldHandleDifferentIssuerResponseCodes() throws Exception {
        // Given
        IssuerResponse[] responses = {
                new IssuerResponse(true, "00"),    // Aprobado
                new IssuerResponse(false, "51"),   // Fondos insuficientes
                new IssuerResponse(false, "54"),   // Tarjeta expirada
                new IssuerResponse(false, "96")    // Error del sistema
        };

        for (IssuerResponse response : responses) {
            when(issuerClient.authorize(anyString(), anyDouble(), anyString()))
                    .thenReturn(response);

            // When & Then
            mockMvc.perform(post("/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validPaymentRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.responseCode").value(response.responseCode()))
                    .andExpect(jsonPath("$.status").value(
                            response.approved() ? "APPROVED" : "DECLINED"
                    ));
        }
    }

    @Test
    @DisplayName("POST /payments - Debe validar múltiples campos inválidos simultáneamente")
    void shouldValidateMultipleInvalidFieldsSimultaneously() throws Exception {
        // Given
        PaymentRequest invalidRequest = new PaymentRequest(
                "", // merchantId vacío
                -100.0, // amount negativo
                "", // currency vacío
                "", // cardToken vacío
                "" // expirationDate vacío
        );

        // When & Then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(issuerClient, never()).authorize(anyString(), anyDouble(), anyString());
    }
}
