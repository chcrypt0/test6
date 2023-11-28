package com.example.test_6.service;

import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;


    public List<Transaction> getHighAmountLastMonth() {
        LocalDateTime startTime = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endTime = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        return transactionRepository.getHighAmountLastMonth(startTime, endTime);
    }

    public Map<String, Integer> getGroupByCurrencyFromLastMonth() {
        LocalDateTime startTime = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endTime = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        List<Object[]> results = transactionRepository.groupTransactionsByCurrencyFrom(startTime, endTime);
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Integer) result[1]
                ));
    }

    public Map<String, Integer> getGroupByCurrencyToLastMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        List<Object[]> results = transactionRepository.groupTransactionsByCurrencyTo(startOfMonth, endOfMonth);
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Integer) result[1]
                ));
    }




}
