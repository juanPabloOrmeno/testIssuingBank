package org.bank.issuingbank.service.impl;

import org.bank.issuingbank.dto.request.PaymentRequest;
import org.bank.issuingbank.dto.response.PaymentResponse;
import org.bank.issuingbank.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    public PaymentServiceImpl() {
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
       return null;
    }

    @Override
    public PaymentResponse getPaymentById(String transactionId) {
        return null;
    }

}
