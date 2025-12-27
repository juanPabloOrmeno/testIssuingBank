package org.bank.issuingbank.service.impl;



import org.bank.issuingbank.dto.request.PaymentRequest;
import org.bank.issuingbank.dto.response.PaymentResponse;
import org.bank.issuingbank.enums.TransactionStatus;
import org.bank.issuingbank.exception.BusinessException;
import org.bank.issuingbank.model.Transaction;
import org.bank.issuingbank.repository.TransactionRepository;
import org.bank.issuingbank.service.PaymentService;
import org.bank.issuingbank.service.external.IssuerClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final TransactionRepository transactionRepository;
    private final IssuerClient issuerClient;

    public PaymentServiceImpl(TransactionRepository transactionRepository,
                              IssuerClient issuerClient) {
        this.transactionRepository = transactionRepository;
        this.issuerClient = issuerClient;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {

        validateRequest(request);

        Transaction transaction = new Transaction();
        transaction.setMerchantId(request.getMerchantId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());

        var issuerResponse = issuerClient.authorize(
                request.getCardToken(),
                request.getAmount(),
                request.getCurrency()
        );

        if (issuerResponse.approved()) {
            transaction.setStatus(TransactionStatus.APPROVED);
        } else {
            transaction.setStatus(TransactionStatus.DECLINED);
        }

        transaction.setResponseCode(issuerResponse.responseCode());
        transactionRepository.save(transaction);

        return new PaymentResponse(
                transaction.getId(),
                transaction.getStatus(),
                transaction.getResponseCode(),
                transaction.getCreatedAt()
        );
    }

    @Override
    public PaymentResponse getPaymentById(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transaction not found"));

        return new PaymentResponse(
                transaction.getId(),
                transaction.getStatus(),
                transaction.getResponseCode(),
                transaction.getCreatedAt()
        );
    }

    private void validateRequest(PaymentRequest request) {
        if (request.getAmount() <= 0) {
            throw new BusinessException("Amount must be greater than zero");
        }
        if (request.getMerchantId() == null || request.getMerchantId().isBlank()) {
            throw new BusinessException("Merchant ID is required");
        }
        if (request.getCardToken() == null || request.getCardToken().isBlank()) {
            throw new BusinessException("Card token is required");
        }
    }
}
