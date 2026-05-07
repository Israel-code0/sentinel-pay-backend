package org.israel.sentinelpay.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WithdrawRequest {
    private String accountNumber;
    private BigDecimal amount;
}
