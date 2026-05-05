package org.israel.sentinelpay.repository;

import org.israel.sentinelpay.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    // This allows us to find a wallet using the Account Number
    Optional<Wallet> findByAccountNumber(String accountNumber);
}
