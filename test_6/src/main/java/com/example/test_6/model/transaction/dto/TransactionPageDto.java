package com.example.test_6.model.transaction.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionPageDto {
    private BigDecimal baseAmount;
    private String currencyFrom;
    private String currencyTo;
    private LocalDateTime exchangeDate;
}
