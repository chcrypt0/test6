package com.example.test_6.service;

import com.example.test_6.exception.TransactionNotFoundException;
import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.model.transaction.command.TransactionCommand;
import com.example.test_6.model.transaction.dto.TransactionDto;
import com.example.test_6.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;
    @Mock
    ModelMapper modelMapper;


    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionService(transactionRepository, modelMapper);
    }

    @Test
    void findById() {
        //given
       Transaction transaction1 = Transaction.builder()
                .id(1L)
                .currencyFrom("USD")
                .currencyTo("EUR")
                .baseAmount(BigDecimal.valueOf(100))
                .convertedAmount(BigDecimal.valueOf(92))
                .exchangeRate(BigDecimal.valueOf(0.92))
                .exchangeDate(LocalDateTime.now())
                .transactionValueInEur(BigDecimal.valueOf(92))
                .build();

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
       Transaction transaction1 = Transaction.builder()
                .id(1L)
                .currencyFrom("USD")
                .currencyTo("EUR")
                .baseAmount(BigDecimal.valueOf(100))
                .convertedAmount(BigDecimal.valueOf(92))
                .exchangeRate(BigDecimal.valueOf(0.92))
                .exchangeDate(LocalDateTime.now())
                .transactionValueInEur(BigDecimal.valueOf(92))
                .build();

       Transaction transaction2 = Transaction.builder()
                .id(2L)
                .currencyFrom("USD")
                .currencyTo("EUR")
                .baseAmount(BigDecimal.valueOf(100))
                .convertedAmount(BigDecimal.valueOf(92))
                .exchangeRate(BigDecimal.valueOf(0.92))
                .exchangeDate(LocalDateTime.now())
                .transactionValueInEur(BigDecimal.valueOf(92))
                .build();
        List<Transaction> expectedTransactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.findAll()).thenReturn(expectedTransactions);

        //when
        List<TransactionDto> actualTransactions = transactionService.findAll();

        //then
        assertEquals(transaction1.getCurrencyFrom(), actualTransactions.get(0).getCurrencyFrom());
        assertEquals(transaction1.getCurrencyTo(), actualTransactions.get(0).getCurrencyTo());
        assertEquals(transaction1.getBaseAmount(), actualTransactions.get(0).getBaseAmount());
        assertEquals(transaction1.getConvertedAmount(), actualTransactions.get(0).getConvertedAmount());
        assertEquals(transaction1.getExchangeRate(), actualTransactions.get(0).getExchangeRate());
        assertEquals(transaction1.getExchangeDate(), actualTransactions.get(0).getExchangeDate());
        assertEquals(transaction1.getTransactionValueInEur(), actualTransactions.get(0).getTransactionValueInEur());

        assertEquals(transaction2.getCurrencyFrom(), actualTransactions.get(1).getCurrencyFrom());
        assertEquals(transaction2.getCurrencyTo(), actualTransactions.get(1).getCurrencyTo());
        assertEquals(transaction2.getBaseAmount(), actualTransactions.get(1).getBaseAmount());
        assertEquals(transaction2.getConvertedAmount(), actualTransactions.get(1).getConvertedAmount());
        assertEquals(transaction2.getExchangeRate(), actualTransactions.get(1).getExchangeRate());
        assertEquals(transaction2.getExchangeDate(), actualTransactions.get(1).getExchangeDate());
        assertEquals(transaction2.getTransactionValueInEur(), actualTransactions.get(1).getTransactionValueInEur());
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

    @Test
    void edit() {
        //given
        Long transactionId = 1L;
        Transaction transaction = new Transaction();
        TransactionDto transactionDto = modelMapper.map(transaction, TransactionDto.class);

        TransactionCommand command = TransactionCommand.builder()
                .currencyFrom("a")
                .currencyTo("a")
                .baseAmount(BigDecimal.valueOf(0))
                .convertedAmount(BigDecimal.valueOf(0))
                .exchangeRate(BigDecimal.valueOf(0))
                .exchangeDate(LocalDateTime.of(2000,1,1,3,4,55,3))
                .build();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        //when
        TransactionDto editedTransaction = transactionService.edit(transactionId, command);

        //then
        assertAll(
                () -> assertEquals("a", editedTransaction.getCurrencyFrom()),
                () -> assertEquals("a", editedTransaction.getCurrencyTo()),
                () -> assertEquals(BigDecimal.valueOf(0), editedTransaction.getBaseAmount()),
                () -> assertEquals(BigDecimal.valueOf(0), editedTransaction.getConvertedAmount()),
                () -> assertEquals(BigDecimal.valueOf(0), editedTransaction.getExchangeRate()),
                () -> assertEquals(LocalDateTime.of(2000,1,1,3,4,55,3), editedTransaction.getExchangeDate())
        );

    }

    @Test
    void editPartially() {
        //given
        Long transactionId = 1L;

        Transaction transaction = Transaction.builder()
                .id(1L)
                .currencyFrom("USD")
                .currencyTo("EUR")
                .baseAmount(BigDecimal.valueOf(100))
                .convertedAmount(BigDecimal.valueOf(92))
                .exchangeRate(BigDecimal.valueOf(0.92))
                .exchangeDate(LocalDateTime.now())
                .transactionValueInEur(BigDecimal.valueOf(92))
                .build();

        TransactionCommand command = TransactionCommand.builder()
                .currencyFrom("a")
                .baseAmount(BigDecimal.valueOf(0))
                .exchangeRate(BigDecimal.valueOf(0))
                .build();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        //when
        TransactionDto editedTransaction = transactionService.editPartially(transactionId, command);

        //then
        assertAll(
                () -> assertEquals(transaction.getCurrencyFrom(), editedTransaction.getCurrencyFrom()),
                () -> assertEquals(transaction.getCurrencyTo(), editedTransaction.getCurrencyTo()),
                () -> assertEquals(transaction.getBaseAmount(), editedTransaction.getBaseAmount()),
                () -> assertEquals(transaction.getConvertedAmount(), editedTransaction.getConvertedAmount()),
                () -> assertEquals(transaction.getExchangeRate(), editedTransaction.getExchangeRate()),
                () -> assertEquals(transaction.getExchangeDate(), editedTransaction.getExchangeDate())
        );
    }
}