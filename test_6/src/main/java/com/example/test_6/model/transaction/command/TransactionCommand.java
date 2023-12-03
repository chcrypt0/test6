package com.example.test_6.model.transaction.command;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionCommand {
    private String currencyFrom;
    private String currencyTo;
    private BigDecimal baseAmount;
}
