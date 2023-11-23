package com.example.test_6.model.calculator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Calculator {

    private BigInteger amount;
    private String currencyFrom;
    private String currencyTo;

}
