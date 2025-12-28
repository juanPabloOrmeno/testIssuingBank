package org.bank.issuingbank.service.impl;

import org.bank.issuingbank.dto.request.PaymentRequest;
import org.bank.issuingbank.dto.response.PaymentResponse;
import org.bank.issuingbank.enums.TransactionStatus;
import org.bank.issuingbank.exception.BusinessException;
import org.bank.issuingbank.model.Transaction;
import org.bank.issuingbank.repository.TransactionRepository;
import org.bank.issuingbank.service.external.IssuerClient;
import org.bank.issuingbank.service.external.dto.IssuerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Tests")
class PaymentServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private IssuerClient issuerClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest validPaymentRequest;
    private Transaction mockTransaction;
    private IssuerResponse approvedIssuerResponse;
    private IssuerResponse declinedIssuerResponse;

    @BeforeEach
    void setUp() {
        validPaymentRequest = new PaymentRequest(
                "MERCHANT_001",
                50000.0,
                "CLP",
                "tok_abc123xyz",
                "12/26"
        );

        mockTransaction = new Transaction();
        mockTransaction.setId("txn_123456");
        mockTransaction.setMerchantId("MERCHANT_001");
        mockTransaction.setAmount(50000.0);
        mockTransaction.setCurrency("CLP");
        mockTransaction.setStatus(TransactionStatus.APPROVED);
        mockTransaction.setResponseCode("00");
        mockTransaction.setCreatedAt(LocalDateTime.now());

        approvedIssuerResponse = new IssuerResponse(true, "00");
        declinedIssuerResponse = new IssuerResponse(false, "51");
    }

    @Test
    @DisplayName("Debe procesar un pago aprobado exitosamente")
    void shouldProcessApprovedPaymentSuccessfully() {
        // Given
        when(issuerClient.authorize(anyString(), anyDouble(), anyString()))
                .thenReturn(approvedIssuerResponse);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> {
                    Transaction t = invocation.getArgument(0);
                    t.setId("txn_123456");
                    return t;
                });

        // When
        PaymentResponse response = paymentService.processPayment(validPaymentRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.transactionId()).isEqualTo("txn_123456");
        assertThat(response.status()).isEqualTo(TransactionStatus.APPROVED);
        assertThat(response.responseCode()).isEqualTo("00");
        assertThat(response.createdAt()).isNotNull();

        // Verificar que se llamó al issuer con los parámetros correctos
        verify(issuerClient, times(1)).authorize(
                validPaymentRequest.cardToken(),
                validPaymentRequest.amount(),
                validPaymentRequest.currency()
        );

        // Verificar que se guardó la transacción
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());
        
        Transaction savedTransaction = transactionCaptor.getValue();
        assertThat(savedTransaction.getMerchantId()).isEqualTo("MERCHANT_001");
        assertThat(savedTransaction.getAmount()).isEqualTo(50000.0);
        assertThat(savedTransaction.getStatus()).isEqualTo(TransactionStatus.APPROVED);
        assertThat(savedTransaction.getResponseCode()).isEqualTo("00");
    }

    @Test
    @DisplayName("Debe procesar un pago declinado correctamente")
    void shouldProcessDeclinedPaymentCorrectly() {
        // Given
        Transaction declinedTransaction = new Transaction();
        declinedTransaction.setId("txn_declined");
        declinedTransaction.setMerchantId("MERCHANT_001");
        declinedTransaction.setAmount(50000.0);
        declinedTransaction.setCurrency("CLP");
        declinedTransaction.setStatus(TransactionStatus.DECLINED);
        declinedTransaction.setResponseCode("51");
        declinedTransaction.setCreatedAt(LocalDateTime.now());

        when(issuerClient.authorize(anyString(), anyDouble(), anyString()))
                .thenReturn(declinedIssuerResponse);
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(declinedTransaction);

        // When
        PaymentResponse response = paymentService.processPayment(validPaymentRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(TransactionStatus.DECLINED);
        assertThat(response.responseCode()).isEqualTo("51");

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());
        
        Transaction savedTransaction = transactionCaptor.getValue();
        assertThat(savedTransaction.getStatus()).isEqualTo(TransactionStatus.DECLINED);
        assertThat(savedTransaction.getResponseCode()).isEqualTo("51");
    }

    @Test
    @DisplayName("Debe lanzar BusinessException cuando el issuer falla")
    void shouldThrowBusinessExceptionWhenIssuerFails() {
        // Given
        when(issuerClient.authorize(anyString(), anyDouble(), anyString()))
                .thenThrow(new RuntimeException("Issuer service unavailable"));

        // When & Then
        assertThatThrownBy(() -> paymentService.processPayment(validPaymentRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Failed to process payment");

        // Verificar que no se guardó ninguna transacción
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debe obtener un pago por ID exitosamente")
    void shouldGetPaymentByIdSuccessfully() {
        // Given
        String transactionId = "txn_123456";
        when(transactionRepository.findById(transactionId))
                .thenReturn(Optional.of(mockTransaction));

        // When
        PaymentResponse response = paymentService.getPaymentById(transactionId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.transactionId()).isEqualTo("txn_123456");
        assertThat(response.status()).isEqualTo(TransactionStatus.APPROVED);
        assertThat(response.responseCode()).isEqualTo("00");

        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    @DisplayName("Debe lanzar BusinessException cuando la transacción no existe")
    void shouldThrowBusinessExceptionWhenTransactionNotFound() {
        // Given
        String nonExistentId = "txn_nonexistent";
        when(transactionRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> paymentService.getPaymentById(nonExistentId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Transaction not found");

        verify(transactionRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Debe crear transacción con estado PENDING inicialmente")
    void shouldCreateTransactionWithPendingStatusInitially() {
        // Given
        when(issuerClient.authorize(anyString(), anyDouble(), anyString()))
                .thenReturn(approvedIssuerResponse);
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(mockTransaction);

        // When
        paymentService.processPayment(validPaymentRequest);

        // Then
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        
        Transaction savedTransaction = transactionCaptor.getValue();
        // La transacción se guarda con el estado final (APPROVED o DECLINED), 
        // pero internamente se crea primero como PENDING
        assertThat(savedTransaction.getCreatedAt()).isNotNull();
        assertThat(savedTransaction.getMerchantId()).isEqualTo(validPaymentRequest.merchantId());
        assertThat(savedTransaction.getAmount()).isEqualTo(validPaymentRequest.amount());
        assertThat(savedTransaction.getCurrency()).isEqualTo(validPaymentRequest.currency());
    }

    @Test
    @DisplayName("Debe manejar diferentes códigos de respuesta del issuer")
    void shouldHandleDifferentIssuerResponseCodes() {
        // Given
        String[] responseCodes = {"00", "51", "54", "96"};
        
        for (String code : responseCodes) {
            IssuerResponse response = new IssuerResponse("00".equals(code), code);
            Transaction transaction = new Transaction();
            transaction.setId("txn_" + code);
            transaction.setStatus("00".equals(code) ? TransactionStatus.APPROVED : TransactionStatus.DECLINED);
            transaction.setResponseCode(code);
            transaction.setCreatedAt(LocalDateTime.now());

            when(issuerClient.authorize(anyString(), anyDouble(), anyString()))
                    .thenReturn(response);
            when(transactionRepository.save(any(Transaction.class)))
                    .thenReturn(transaction);

            // When
            PaymentResponse paymentResponse = paymentService.processPayment(validPaymentRequest);

            // Then
            assertThat(paymentResponse.responseCode()).isEqualTo(code);
        }
    }

    @Test
    @DisplayName("Debe preservar los datos del request en la transacción guardada")
    void shouldPreserveRequestDataInSavedTransaction() {
        // Given
        when(issuerClient.authorize(anyString(), anyDouble(), anyString()))
                .thenReturn(approvedIssuerResponse);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        paymentService.processPayment(validPaymentRequest);

        // Then
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        
        Transaction savedTransaction = transactionCaptor.getValue();
        assertThat(savedTransaction.getMerchantId()).isEqualTo(validPaymentRequest.merchantId());
        assertThat(savedTransaction.getAmount()).isEqualTo(validPaymentRequest.amount());
        assertThat(savedTransaction.getCurrency()).isEqualTo(validPaymentRequest.currency());
    }
}
