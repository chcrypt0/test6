package com.example.test_6.service;

import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;


    public List<Transaction> getHighAmountLastMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        return transactionRepository.getHighAmountLastMonth(startOfMonth, endOfMonth);
    }

    public Map<String, Integer> getGroupByCurrencyFromLastMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        List<Object[]> results = transactionRepository.countTransactionsByCurrencyFrom(startOfMonth, endOfMonth);

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Integer) result[1]
                ));
    }

    public Map<String, Integer> getGroupByCurrencyToLastMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        List<Object[]> results = transactionRepository.countTransactionsByCurrencyTo(startOfMonth, endOfMonth);

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Integer) result[1]
                ));
    }

}
