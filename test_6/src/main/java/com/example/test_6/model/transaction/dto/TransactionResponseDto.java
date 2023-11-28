package com.example.test_6.model.transaction.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionResponseDto {
    private BigDecimal convertedAmount;
}
