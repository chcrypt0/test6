package com.example.test_6.controller;

import com.example.test_6.model.reportType.ReportType;
import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/calculator")
@RequiredArgsConstructor
public class ReportController {

    private ReportType type;

    private ReportService reportService;

    @GetMapping("/{reportName}")
    public ResponseEntity<?> generateReport(@PathVariable String reportName) {
        try {
            type = ReportType.valueOf(reportName);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>("Invalid report name", HttpStatus.BAD_REQUEST);
        }

        switch (type) {
            case HIGH_AMOUNT_LAST_MONTH:
                List<Transaction> highAmountExchanges = reportService.getHighAmountLastMonth();
                return new ResponseEntity<>(highAmountExchanges, HttpStatus.OK);

            case GROUP_BY_CURRENCY_FROM_LAST_MONTH:
                Map<String, Integer> exchangesByCurrencyFrom = reportService.getGroupByCurrencyFromLastMonth();
                return new ResponseEntity<>(exchangesByCurrencyFrom, HttpStatus.OK);

            case GROUP_BY_CURRENCY_TO_LAST_MONTH:
                Map<String, Integer> exchangesByCurrencyTo = reportService.getGroupByCurrencyToLastMonth();
                return new ResponseEntity<>(exchangesByCurrencyTo, HttpStatus.OK);

            default:
                return new ResponseEntity<>("Invalid report name", HttpStatus.BAD_REQUEST);
        }
    }
}
