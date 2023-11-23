package com.example.test_6.model.calculator.command;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CalculatorCommand {
    private BigInteger amount;
    private String currencyFrom;
    private String currencyTo;
}
