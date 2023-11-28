package com.example.test_6.model.transaction.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionDto {
    private String currencyFrom;
    private String currencyTo;
    private BigDecimal baseAmount;
    private BigDecimal convertedAmount;
    private BigDecimal exchangeRate;
    private LocalDateTime exchangeDate;
    private BigDecimal transactionValueInEur;
}
