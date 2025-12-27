package org.bank.issuingbank.service.external;

import org.bank.issuingbank.service.external.dto.IssuerResponse;

public interface IssuerClient {

    IssuerResponse authorize(String cardToken, Double amount, String currency);

}
