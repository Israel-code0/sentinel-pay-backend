package org.israel.sentinelpay.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class TransferRequest {
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
}
