package com.example.test_6.service;

import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        reportService = new ReportService(transactionRepository);
    }

    @Test
    void getHighAmountLastMonth() {
        //given
        List<Transaction> mockTransactions = new ArrayList<>();
        mockTransactions.add(new Transaction());
        mockTransactions.add(new Transaction());

        when(transactionRepository.getHighAmountLastMonth(Mockito.any(), Mockito.any()))
                .thenReturn(mockTransactions);

        //when
        List<Transaction> result = reportService.getHighAmountLastMonth();

        //then
        assertNotNull(result);
        assertEquals(2, result.size());
    }


    @Test
    void getGroupByCurrencyFromLastMonth() {
        //given
        List<Object[]> mockDataForCurrencyFrom = new ArrayList<>();
        mockDataForCurrencyFrom.add(new Object[]{"USD", 5});
        mockDataForCurrencyFrom.add(new Object[]{"EUR", 3});
        when(transactionRepository.groupTransactionsByCurrencyFrom(Mockito.any(), Mockito.any()))
                .thenReturn(mockDataForCurrencyFrom);

        //when
        Map<String, Integer> result = reportService.getGroupByCurrencyFromLastMonth();

        //then
        assertEquals(2, result.size());
        assertEquals(5, result.get("USD"));
        assertEquals(3, result.get("EUR"));
    }

    @Test
    void getGroupByCurrencyToLastMonth() {
        //given
        List<Object[]> mockDataForCurrencyTo = new ArrayList<>();
        mockDataForCurrencyTo.add(new Object[]{"GBP", 2});
        mockDataForCurrencyTo.add(new Object[]{"JPY", 4});
        when(transactionRepository.groupTransactionsByCurrencyTo(Mockito.any(), Mockito.any()))
                .thenReturn(mockDataForCurrencyTo);

        //when
        Map<String, Integer> result = reportService.getGroupByCurrencyToLastMonth();

        //then
        assertEquals(2, result.size());
        assertEquals(2, result.get("GBP"));
        assertEquals(4, result.get("JPY"));
    }
}