package com.example.test_6.model.transaction.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionResponseDto {
    private BigDecimal convertedAmount;
}
