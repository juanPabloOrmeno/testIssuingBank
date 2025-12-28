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

        Transaction transaction = new Transaction();
        transaction.setMerchantId(request.merchantId());
        transaction.setAmount(request.amount());
        transaction.setCurrency(request.currency());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());

        var issuerResponse = issuerClient.authorize(
                request.cardToken(),
                request.amount(),
                request.currency()
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
}
