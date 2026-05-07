package org.israel.sentinelpay.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionReference;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING) // This tells JPA to use the String name (TRANSFER/DEPOSIT)
    private TransactionType type;

    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public Transaction() {
        // Automatically generate a unique reference for every transaction
        this.transactionReference = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
}
