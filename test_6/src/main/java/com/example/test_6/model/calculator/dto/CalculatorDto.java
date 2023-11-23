package com.example.test_6.model.calculator.dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CalculatorDto {
    private BigInteger amount;
    private String currencyFrom;
    private String currencyTo;
}
