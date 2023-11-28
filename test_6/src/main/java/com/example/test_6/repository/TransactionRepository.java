package com.example.test_6.repository;

import com.example.test_6.model.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.exchangeDate BETWEEN :startOfMonth AND :endOfMonth AND t.transactionValueInEur > 15000")
    List<Transaction> getHighAmountLastMonth(LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT currencyFrom, COUNT(currencyFrom) FROM Transaction " +
            "WHERE exchangeDate BETWEEN :start AND :end " +
            "GROUP BY currencyFrom")
    List<Object[]> groupTransactionsByCurrencyFrom(@Param("start") LocalDateTime startTime, @Param("end") LocalDateTime endTime);

    @Query("SELECT currencyTo, COUNT(currencyTo) FROM Transaction " +
            "WHERE exchangeDate BETWEEN :start AND :endOf " +
            "GROUP BY currencyTo")
    List<Object[]> groupTransactionsByCurrencyTo(@Param("start") LocalDateTime startTime, @Param("end") LocalDateTime endTime);
}
