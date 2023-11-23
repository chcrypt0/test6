package com.example.test_6.repository;

import com.example.test_6.model.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.exchangeDate BETWEEN :startOfMonth AND :endOfMonth AND t.moreThan15kEur = true")
    List<Transaction> getHighAmountLastMonth(LocalDateTime startOfMonth, LocalDateTime endOfMonth);

    @Query("SELECT currencyFrom, COUNT(currencyFrom) FROM Transaction " +
            "WHERE exchangeDate BETWEEN :startOfMonth AND :endOfMonth " +
            "GROUP BY currencyFrom")
    List<Object[]> countTransactionsByCurrencyFrom(@Param("startOfMonth") LocalDateTime startOfMonth,
                                                   @Param("endOfMonth") LocalDateTime endOfMonth);

    @Query("SELECT currencyTo, COUNT(currencyTo) FROM Transaction " +
            "WHERE exchangeDate BETWEEN :startOfMonth AND :endOfMonth " +
            "GROUP BY currencyTo")
    List<Object[]> countTransactionsByCurrencyTo(@Param("startOfMonth") LocalDateTime startOfMonth,
                                                 @Param("endOfMonth") LocalDateTime endOfMonth);
}
