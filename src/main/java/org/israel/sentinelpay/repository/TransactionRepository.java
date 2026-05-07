package org.israel.sentinelpay.repository;

import org.israel.sentinelpay.model.Transaction;
import org.israel.sentinelpay.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // All transactions involving a specific account number
    Page<Transaction> findBySenderAccountNumberOrReceiverAccountNumber(
            String sender, String receiver, Pageable pageable);

    Page<Transaction> findBySenderAccountNumberOrReceiverAccountNumberAndType(
            String sender, String receiver, TransactionType type, Pageable pageable);
}
