package com.example.test_6.controller;

import com.example.test_6.model.calculator.command.CalculatorCommand;
import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.model.transaction.dto.TransactionDto;
import com.example.test_6.model.transaction.dto.TransactionResponseDto;
import com.example.test_6.service.CalculatorService;
import com.example.test_6.service.TransactionService;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calculator")
@RequiredArgsConstructor
public class CalculatorController {

    private final CalculatorService calculatorService;
    private final ModelMapper modelMapper;

    @GetMapping("/exchange")
    public ResponseEntity<TransactionDto> convertCurrency(@RequestBody CalculatorCommand command) {
        JsonObject response = calculatorService.convertCurrency(
                command.getCurrencyFrom(),
                command.getCurrencyTo(),
                command.getAmount()
        );
        //TransactionResponseDto responseDto = modelMapper.map(response, TransactionResponseDto.class);
        TransactionDto responseDto = TransactionDto.builder()
                .currencyFrom(response.get("base").getAsString())
                .currencyTo(response.get("target").getAsString())
                .baseAmount(response.get("base_amount").getAsBigDecimal())
                .convertedAmount(response.get("converted_amount").getAsBigDecimal())
                .exchangeRate(response.get("exchange_rate").getAsBigDecimal())
                .exchangeDate(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


}
