package com.example.test_6.service;

import com.example.test_6.exception.TransactionNotFoundException;
import com.example.test_6.mappings.TransactionToTransactionDtoConverter;
import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.model.transaction.command.TransactionCommand;
import com.example.test_6.model.transaction.dto.TransactionDto;
import com.example.test_6.repository.TransactionRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Assert;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionService transactionService;
    @Mock
    private Environment environment;
    private static final String API_RESPONSE = "{\"base\":\"PLN\",\"last_updated\":1701177300,\"exchange_rates\":{\"EUR\":10,\"USD\":10}}";
    @Mock
    ModelMapper modelMapper;
    TransactionCommand command;
    JsonObject exchangeRatesJson;
    Transaction transactionAfterExchange;
    Transaction transaction1;
    Transaction transaction2;


    @BeforeEach
    void setUpRepo() {
        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionService(transactionRepository, modelMapper, environment);
    }
    @BeforeEach
    void setUpExampleTransactions(){
         transaction1 = Transaction.builder()
                .id(1L)
                .currencyFrom("USD")
                .currencyTo("EUR")
                .baseAmount(BigDecimal.valueOf(100))
                .convertedAmount(BigDecimal.valueOf(92))
                .exchangeRate(BigDecimal.valueOf(0.92))
                .exchangeDate(LocalDateTime.now())
                .transactionValueInEur(BigDecimal.valueOf(92))
                .build();

         transaction2 = Transaction.builder()
                .id(2L)
                .currencyFrom("USD")
                .currencyTo("EUR")
                .baseAmount(BigDecimal.valueOf(100))
                .convertedAmount(BigDecimal.valueOf(92))
                .exchangeRate(BigDecimal.valueOf(0.92))
                .exchangeDate(LocalDateTime.now())
                .transactionValueInEur(BigDecimal.valueOf(92))
                .build();
    }
    @BeforeEach
    void setUpExampleExchangeData() {
        command = TransactionCommand.builder()
                .currencyFrom("USD")
                .currencyTo("PLN")
                .baseAmount(new BigDecimal(100))
                .build();

        exchangeRatesJson = JsonParser.parseString(API_RESPONSE).getAsJsonObject().getAsJsonObject("exchange_rates");
        transactionAfterExchange = Transaction.builder()
                .id(1L)
                .currencyFrom("PLN")
                .currencyTo("USD")
                .baseAmount(new BigDecimal(100))
                .convertedAmount(new BigDecimal(1000).multiply(exchangeRatesJson.get("USD").getAsBigDecimal()))
                .exchangeRate(exchangeRatesJson.get("USD").getAsBigDecimal())
                .exchangeDate(LocalDateTime.now())
                .transactionValueInEur(new BigDecimal(1000))
                .build();

       lenient().when(modelMapper.map(command, Transaction.class)).thenReturn(transactionAfterExchange);
    }
    @Test
    void convertCurrency() {
        //given
        when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});

        //when
        Transaction result = transactionService.convertCurrency(command);

        //then
        assertNotNull(result);
        assertEquals(result.getCurrencyFrom(), transactionAfterExchange.getCurrencyFrom());
        assertEquals(result.getCurrencyTo(), transactionAfterExchange.getCurrencyTo());
        assertEquals(result.getBaseAmount(), transactionAfterExchange.getBaseAmount());
        assertEquals(result.getConvertedAmount(), transactionAfterExchange.getConvertedAmount());
        assertEquals(result.getExchangeRate(), transactionAfterExchange.getExchangeRate());
        assertEquals(result.getTransactionValueInEur(), transactionAfterExchange.getTransactionValueInEur());
    }

    @Test
    void findById() {
        //given
        Long transactionId = 1L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.ofNullable(transaction1));

        //when
        Transaction actualTransaction = transactionRepository.findById(transactionId).orElseThrow(() -> new TransactionNotFoundException(String.format("Transaction with id: %s not found", transactionId)));

        //then
        assertEquals(actualTransaction.getId(), transaction1.getId());
        assertEquals(actualTransaction.getCurrencyFrom(), transaction1.getCurrencyFrom());
        assertEquals(actualTransaction.getCurrencyTo(), transaction1.getCurrencyTo());
        assertEquals(actualTransaction.getBaseAmount(), transaction1.getBaseAmount());
        assertEquals(actualTransaction.getConvertedAmount(), transaction1.getConvertedAmount());
        assertEquals(actualTransaction.getExchangeRate(), transaction1.getExchangeRate());
        assertEquals(actualTransaction.getExchangeDate(), transaction1.getExchangeDate());
    }

    @Test
    void findByIdThrowsException() {
        //given
        Long transactionId = 69L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        //when + then
        assertThrows(TransactionNotFoundException.class, () -> transactionService.findById(transactionId), String.format("Transaction with id: %s not found", transactionId));
    }


    @Test
    void findAll() {
        //given
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        TransactionDto transactionDto1 = TransactionDto.builder()
                .currencyFrom(transaction1.getCurrencyFrom())
                .currencyTo(transaction1.getCurrencyTo())
                .baseAmount(transaction1.getBaseAmount())
                .convertedAmount(transaction1.getConvertedAmount())
                .exchangeRate(transaction1.getExchangeRate())
                .exchangeDate(transaction1.getExchangeDate())
                .transactionValueInEur(transaction1.getTransactionValueInEur())
                .build();

        TransactionDto transactionDto2 = TransactionDto.builder()
                .currencyFrom(transaction2.getCurrencyFrom())
                .currencyTo(transaction2.getCurrencyTo())
                .baseAmount(transaction2.getBaseAmount())
                .convertedAmount(transaction2.getConvertedAmount())
                .exchangeRate(transaction2.getExchangeRate())
                .exchangeDate(transaction2.getExchangeDate())
                .transactionValueInEur(transaction2.getTransactionValueInEur())
                .build();


        when(transactionRepository.findAll()).thenReturn(transactions);
        when(modelMapper.map(transaction1, TransactionDto.class)).thenReturn(transactionDto1);
        when(modelMapper.map(transaction2, TransactionDto.class)).thenReturn(transactionDto2);

        //when
        List<TransactionDto> actualTransactions = transactionService.findAll();

        //then
        assertEquals(2, actualTransactions.size());

        TransactionDto receivedDto1 = actualTransactions.get(0);
        assertEquals(transaction1.getCurrencyFrom(), receivedDto1.getCurrencyFrom());
        assertEquals(transaction1.getCurrencyTo(), receivedDto1.getCurrencyTo());
        assertEquals(transaction1.getBaseAmount(), receivedDto1.getBaseAmount());
        assertEquals(transaction1.getConvertedAmount(), receivedDto1.getConvertedAmount());
        assertEquals(transaction1.getExchangeRate(), receivedDto1.getExchangeRate());
        assertEquals(transaction1.getExchangeDate(), receivedDto1.getExchangeDate());
        assertEquals(transaction1.getTransactionValueInEur(), receivedDto1.getTransactionValueInEur());

        TransactionDto receivedDto2 = actualTransactions.get(1);
        assertEquals(transaction2.getCurrencyFrom(), receivedDto2.getCurrencyFrom());
        assertEquals(transaction2.getCurrencyTo(), receivedDto2.getCurrencyTo());
        assertEquals(transaction2.getBaseAmount(), receivedDto2.getBaseAmount());
        assertEquals(transaction2.getConvertedAmount(), receivedDto2.getConvertedAmount());
        assertEquals(transaction2.getExchangeRate(), receivedDto2.getExchangeRate());
        assertEquals(transaction2.getExchangeDate(), receivedDto2.getExchangeDate());
        assertEquals(transaction2.getTransactionValueInEur(), receivedDto2.getTransactionValueInEur());
    }


    @Test
    void deleteById() {
        //given
        Long patientId = 1L;
        doNothing().when(transactionRepository).deleteById(patientId);

        //when
        transactionService.deleteById(patientId);

        //then
        verify(transactionRepository, times(1)).deleteById(patientId);

    }
}