package org.bank.issuingbank.repository;

import org.bank.issuingbank.model.Transaction;
import org.bank.issuingbank.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // Buscar por estado
    List<Transaction> findByStatus(TransactionStatus status);

    // Buscar por comercio
    List<Transaction> findByMerchantId(String merchantId);

    // Buscar por comercio y estado
    List<Transaction> findByMerchantIdAndStatus(String merchantId, TransactionStatus status);
}
