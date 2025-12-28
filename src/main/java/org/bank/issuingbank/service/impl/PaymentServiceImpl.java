package org.bank.issuingbank.service.impl;



import org.bank.issuingbank.dto.request.PaymentRequest;
import org.bank.issuingbank.dto.response.PaymentResponse;
import org.bank.issuingbank.enums.TransactionStatus;
import org.bank.issuingbank.exception.BusinessException;
import org.bank.issuingbank.model.Transaction;
import org.bank.issuingbank.repository.TransactionRepository;
import org.bank.issuingbank.service.PaymentService;
import org.bank.issuingbank.service.external.IssuerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final IssuerClient issuerClient;

    public PaymentServiceImpl(TransactionRepository transactionRepository,
                              IssuerClient issuerClient) {
        this.transactionRepository = transactionRepository;
        this.issuerClient = issuerClient;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {

        log.info("Processing payment - merchantId: {}, amount: {}, currency: {}",
                request.merchantId(), request.amount(), request.currency());

        Transaction transaction = new Transaction();
        transaction.setMerchantId(request.merchantId());
        transaction.setAmount(request.amount());
        transaction.setCurrency(request.currency());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());

        log.debug("Transaction created with PENDING status - merchantId: {}", request.merchantId());

        try {
            var issuerResponse = issuerClient.authorize(
                    request.cardToken(),
                    request.amount(),
                    request.currency()
            );

            log.info("Issuer response received - approved: {}, responseCode: {}",
                    issuerResponse.approved(), issuerResponse.responseCode());

            if (issuerResponse.approved()) {
                transaction.setStatus(TransactionStatus.APPROVED);
                log.info("Payment APPROVED - transactionId will be generated");
            } else {
                transaction.setStatus(TransactionStatus.DECLINED);
                log.warn("Payment DECLINED - responseCode: {}", issuerResponse.responseCode());
            }

            transaction.setResponseCode(issuerResponse.responseCode());
            transactionRepository.save(transaction);

            log.info("Transaction saved successfully - transactionId: {}, status: {}",
                    transaction.getId(), transaction.getStatus());

            return new PaymentResponse(
                    transaction.getId(),
                    transaction.getStatus(),
                    transaction.getResponseCode(),
                    transaction.getCreatedAt()
            );

        } catch (Exception e) {
            log.error("Error processing payment - merchantId: {}, amount: {}, error: {}",
                    request.merchantId(), request.amount(), e.getMessage(), e);
            throw new BusinessException("Failed to process payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponse getPaymentById(String transactionId) {

        log.debug("Fetching payment by transactionId: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.warn("Transaction not found - transactionId: {}", transactionId);
                    return new BusinessException("Transaction not found");
                });

        log.info("Payment retrieved successfully - transactionId: {}, status: {}",
                transactionId, transaction.getStatus());

        return new PaymentResponse(
                transaction.getId(),
                transaction.getStatus(),
                transaction.getResponseCode(),
                transaction.getCreatedAt()
        );
    }
}
