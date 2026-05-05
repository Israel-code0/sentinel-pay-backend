package org.israel.sentinelpay.service;

import org.israel.sentinelpay.model.Transaction;
import org.israel.sentinelpay.model.User;
import org.israel.sentinelpay.model.Wallet;
import org.israel.sentinelpay.repository.UserRepository;
import org.israel.sentinelpay.repository.WalletRepository;
import org.israel.sentinelpay.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TransferService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void transferMoney(String senderAcc, String receiverAcc, BigDecimal amount) {
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
            throw new RuntimeException("Insufficient funds!");
        }

        // Execute
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        // Save changes to MySQL
        walletRepository.save(sender);
        walletRepository.save(receiver);

        // Audit Log
        Transaction record = new Transaction();
        record.setSenderAccountNumber(senderAcc);
        record.setReceiverAccountNumber(receiverAcc);
        record.setAmount(amount);

        transactionRepository.save(record);
    }
    // Get transaction history
    public List<Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findBySenderAccountNumberOrReceiverAccountNumberOrderByCreatedAtDesc(
                accountNumber, accountNumber
        );
    }

    public BigDecimal getBalance(String accountNumber) {
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return wallet.getBalance();
    }

    @Transactional
    public User createUser(User user) {
        // Automatically generate a unique 10-digit account number
        String newAccountNumber = String.valueOf((long) (Math.random() * 9_000_000_000L) + 1_000_000_000L);

        // Set it as the user's primary account and save the user
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
}