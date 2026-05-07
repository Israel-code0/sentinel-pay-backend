package org.israel.sentinelpay.controller;

import lombok.RequiredArgsConstructor;
import org.israel.sentinelpay.dto.ApiResponse;
import org.israel.sentinelpay.dto.DepositRequest;
import org.israel.sentinelpay.dto.TransferRequest;
import org.israel.sentinelpay.model.Transaction;
import org.israel.sentinelpay.model.User;
import org.israel.sentinelpay.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.israel.sentinelpay.repository.UserRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final TransferService transferService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@RequestBody User user) {
        User savedUser = transferService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", savedUser));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Transaction>> transfer(@RequestBody TransferRequest request) {
        Transaction transaction = transferService.transfer(
                request.getSenderAccountNumber(),
                request.getReceiverAccountNumber(),
                request.getAmount()
        );
        return ResponseEntity.ok(
                ApiResponse.success("Transfer successful", transaction)
        );
    }

    @GetMapping("/{accountNumber}/history") // The variable is {accountNumber}
    public List<Transaction> getHistory(@PathVariable String accountNumber) { // This must match
        return transferService.getTransactionHistory(accountNumber);
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable String accountNumber) {
        BigDecimal balance = transferService.getBalance(accountNumber);

        // A simple map to return a JSON object
        Map<String, Object> response = new HashMap<>();
        response.put("accountNumber", accountNumber);
        response.put("balance", balance);
        response.put("currency", "NGN");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<User> getUserProfile(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<Transaction>> deposit(@RequestBody DepositRequest request) {
        Transaction transaction = transferService.deposit(
                request.getAccountNumber(),
                request.getAmount()
        );
        return ResponseEntity.ok(
                ApiResponse.success("Deposit successful", transaction)
        );
    }
}
