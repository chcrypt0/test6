package com.example.test_6.repository;

import com.example.test_6.model.transaction.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void getHighAmountLastMonthTest() {
        LocalDateTime startOfMonth = LocalDateTime.of(2023, 10, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2023, 10, 31, 23, 59, 59);

        List<Transaction> highAmountTransactions = transactionRepository.getHighAmountLastMonth(startOfMonth, endOfMonth);

        for (Transaction transaction : highAmountTransactions) {
            assertTrue(transaction.getTransactionValueInEur().compareTo(BigDecimal.valueOf(15000)) > 0);
            assertTrue(transaction.getExchangeDate().isAfter(startOfMonth) || transaction.getExchangeDate().isEqual(startOfMonth));
            assertTrue(transaction.getExchangeDate().isBefore(endOfMonth) || transaction.getExchangeDate().isEqual(endOfMonth));
        }
    }



    @Test
    void groupTransactionsByCurrencyFrom() {
        LocalDateTime startOfMonth = LocalDateTime.of(2023, 10, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2023, 10, 31, 23, 59, 59);

        List<Object[]> results = transactionRepository.groupTransactionsByCurrencyFrom(startOfMonth, endOfMonth);

        Map<String, Long> expectedCounts = new HashMap<>();
        expectedCounts.put("USD", 4L); // Liczba wystąpień USD jako currency_from
        expectedCounts.put("GBP", 4L); // Liczba wystąpień GBP jako currency_from
        expectedCounts.put("EUR", 4L); // Liczba wystąpień EUR jako currency_from

        for (Object[] result : results) {
            String currency = (String) result[0];
            Long count = (Long) result[1];
            assertTrue(expectedCounts.containsKey(currency));
            assertEquals(expectedCounts.get(currency), count);
        }
    }

    @Test
    void groupTransactionsByCurrencyToTest() {
        LocalDateTime startOfMonth = LocalDateTime.of(2023, 10, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(2023, 10, 31, 23, 59, 59);

        List<Object[]> results = transactionRepository.groupTransactionsByCurrencyTo(startOfMonth, endOfMonth);

        Map<String, Long> expectedCounts = new HashMap<>();
        expectedCounts.put("EUR", 4L); // Liczba wystąpień EUR jako currency_to
        expectedCounts.put("USD", 4L); // Liczba wystąpień USD jako currency_to
        expectedCounts.put("GBP", 4L); // Liczba wystąpień GBP jako currency_to

        for (Object[] result : results) {
            String currency = (String) result[0];
            Long count = (Long) result[1];
            assertTrue(expectedCounts.containsKey(currency));
            assertEquals(expectedCounts.get(currency), count);
        }
    }

}