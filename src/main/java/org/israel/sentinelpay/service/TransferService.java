package org.israel.sentinelpay.service;

import lombok.RequiredArgsConstructor;
import org.israel.sentinelpay.exception.FintechException;
import org.israel.sentinelpay.model.*;
import org.israel.sentinelpay.repository.UserRepository;
import org.israel.sentinelpay.repository.WalletRepository;
import org.israel.sentinelpay.repository.TransactionRepository;
import org.israel.sentinelpay.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Transaction transfer(String senderAcc, String receiverAcc, BigDecimal amount) {
        // Validation: Amount must be positive
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }
        // Find the wallets
        Wallet sender = walletRepository.findByAccountNumber(senderAcc)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Wallet receiver = walletRepository.findByAccountNumber(receiverAcc)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        // Check Balance
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new FintechException("Insufficient funds!");
        }

        // Execute
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        // Save changes to MySQL
        walletRepository.save(sender);
        walletRepository.save(receiver);

        // Audit Log
        Transaction transaction = new Transaction();
        transaction.setSenderAccountNumber(senderAcc);
        transaction.setReceiverAccountNumber(receiverAcc);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setCreatedAt(LocalDateTime.now());

       return transactionRepository.save(transaction);
    }
    // Get transaction history
    public List<Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findBySenderAccountNumberOrReceiverAccountNumberOrderByCreatedAtDesc(
                accountNumber, accountNumber
        );
    }

    public BigDecimal getBalance(String accountNumber) {
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new FintechException("Account not found"));
        return wallet.getBalance();
    }

    @Transactional
    public User createUser(User user) {
        // Automatically generate a unique 10-digit account number
        String newAccountNumber = String.valueOf((long) (Math.random() * 9_000_000_000L) + 1_000_000_000L);

        //  Set it as the user's primary account and save the user
        user.setPrimaryAccountNumber(newAccountNumber);
        User savedUser = userRepository.save(user);

        // Create a starting wallet for the new user
        Wallet wallet = new Wallet();
        wallet.setAccountNumber(newAccountNumber);
        wallet.setBalance(BigDecimal.ZERO); // Start with 0.00
        wallet.setUser(savedUser); // Link the wallet to the user

        walletRepository.save(wallet);

        savedUser.setWallets(java.util.List.of(wallet));

        return savedUser;
    }

    @Transactional
    public Transaction deposit(String accountNumber, BigDecimal amount) {
        // 1. Find the wallet
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new FintechException("Account not found"));

        // 2. Update balance
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        // 3. Create a Transaction record for the audit trail
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(UUID.randomUUID().toString());
        transaction.setSenderAccountNumber("SYSTEM_DEPOSIT"); // Mark as an external deposit
        transaction.setReceiverAccountNumber(accountNumber);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setCreatedAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
}