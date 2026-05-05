package org.israel.sentinelpay.controller;

import org.israel.sentinelpay.dto.TransferRequest;
import org.israel.sentinelpay.model.Transaction;
import org.israel.sentinelpay.model.User;
import org.israel.sentinelpay.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.israel.sentinelpay.repository.UserRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private TransferService transferService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferRequest request) {
        transferService.transferMoney(
                request.getSenderAccountNumber(),
                request.getReceiverAccountNumber(),
                request.getAmount()
        );
        return "Transfer Successful!";
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

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return transferService.createUser(user);
    }
}
