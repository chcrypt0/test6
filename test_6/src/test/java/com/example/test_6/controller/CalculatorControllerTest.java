package com.example.test_6.controller;

import com.example.test_6.model.transaction.dto.TransactionDto;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorControllerTest {

    @Test
    void convertCurrency() {
    }

    @Test
    public void testMapping() {
        ModelMapper modelMapper = new ModelMapper();
        String json = "{ \"currencyFrom\": \"USD\", \"currencyTo\": \"EUR\", \"baseAmount\": 100, \"convertedAmount\": 85, \"exchangeRate\": 0.85, \"exchangeDate\": \"2023-11-23T12:00:00\" }";
        JsonObject jsonResponse = JsonParser.parseString(json).getAsJsonObject();
        TransactionDto responseDto = modelMapper.map(jsonResponse, TransactionDto.class);

        System.out.println(responseDto); // Wydrukuj, aby sprawdzić wynik mapowania
    }

    @Test
    public void testMapping2() {
        String json = "{ \"currencyFrom\": \"USD\", \"currencyTo\": \"EUR\", \"baseAmount\": 100, \"convertedAmount\": 85, \"exchangeRate\": 0.85, \"exchangeDate\": \"2023-11-23T12:00:00\" }";
        JsonObject jsonResponse = JsonParser.parseString(json).getAsJsonObject();

        TransactionDto responseDto = TransactionDto.builder()
                .currencyFrom(jsonResponse.get("base").getAsString())
                .currencyTo(jsonResponse.get("target").getAsString())
                .baseAmount(jsonResponse.get("base_amount").getAsBigDecimal())
                .convertedAmount(jsonResponse.get("converted_amount").getAsBigDecimal())
                .exchangeRate(jsonResponse.get("exchange_rate").getAsBigDecimal())
                .exchangeDate(LocalDateTime.now())
                .build();

        System.out.println(responseDto); // Wydrukuj, aby sprawdzić wynik mapowania
    }


}