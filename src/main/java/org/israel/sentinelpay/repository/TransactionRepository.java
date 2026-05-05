package org.israel.sentinelpay.repository;

import org.israel.sentinelpay.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // All transactions involving a specific account number
    List<Transaction> findBySenderAccountNumberOrReceiverAccountNumberOrderByCreatedAtDesc(
            String sender, String receiver
    );
}
