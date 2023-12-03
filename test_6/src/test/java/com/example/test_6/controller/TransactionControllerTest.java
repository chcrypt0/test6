package com.example.test_6.controller;

import com.example.test_6.Test6Application;
import com.example.test_6.model.transaction.command.TransactionCommand;
import com.example.test_6.model.transaction.dto.TransactionDto;
import com.example.test_6.model.transaction.dto.TransactionResponseDto;
import com.example.test_6.repository.TransactionRepository;
import com.example.test_6.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = Test6Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    TransactionCommand command;
    TransactionResponseDto responseDto;

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private Environment environment;
    @InjectMocks
    private TransactionService transactionService;



    @Autowired
    public TransactionControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }


    @BeforeEach
    void setUpData(){
         command = TransactionCommand.builder()
                .currencyFrom("USD")
                .currencyTo("EUR")
                .baseAmount(new BigDecimal("1000"))
                .build();

         responseDto = TransactionResponseDto.builder()
                .convertedAmount(new BigDecimal("920"))
                .build();

         transactionRepository = mock(TransactionRepository.class);
         transactionService = new TransactionService(transactionRepository, modelMapper,environment);
    }


    @Test
    void convertCurrency() throws Exception {
        //when
        when(transactionService.convertCurrency(any())).thenReturn(responseDto);

        //then
        mockMvc.perform(post("/api/v1/calculator/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.convertedAmount").value(responseDto.getConvertedAmount()));
    }

    @Test
    void findById() throws Exception {
        // Given
        TransactionDto transactionDto = TransactionDto.builder()
                .id(1L) // przykładowe ID
                .currencyFrom("USD")
                .currencyTo("EUR")
                .baseAmount(new BigDecimal("1000"))
                .convertedAmount(new BigDecimal("920"))
                .exchangeRate(new BigDecimal("0.92"))
                .exchangeDate(LocalDateTime.now())
                .transactionValueInEur(new BigDecimal("5200"))
                .build();

        when(transactionService.findById(anyLong())).thenReturn(transactionDto);

        // When & Then
        mockMvc.perform(get("/api/v1/calculator/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionDto.getId()))
                .andExpect(jsonPath("$.currencyFrom").value(transactionDto.getCurrencyFrom()))
                .andExpect(jsonPath("$.currencyTo").value(transactionDto.getCurrencyTo()))
                .andExpect(jsonPath("$.baseAmount").value(transactionDto.getBaseAmount()))
                .andExpect(jsonPath("$.convertedAmount").value(transactionDto.getConvertedAmount()))
                .andExpect(jsonPath("$.exchangeRate").value(transactionDto.getExchangeRate()))
                .andExpect(jsonPath("$.transactionValueInEur").value(transactionDto.getTransactionValueInEur()));
    }

    @Test
    void findAll() throws Exception {
        //when+then
        //korzystamy z zasobow z csv
        mockMvc.perform(get("/api/v1/calculator"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete("/api/v1/calculator/{id}", 1))
                .andExpect(status().isNoContent());
    }
}