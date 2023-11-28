package com.example.test_6.controller;

import com.example.test_6.service.ReportService;
import com.example.test_6.strategies.ReportStrategy;
import com.example.test_6.strategies.reportStrategies.GroupByCurrencyFromLastMonthReportStrategy;
import com.example.test_6.strategies.reportStrategies.GroupByCurrencyToLastMonthReportStrategy;
import com.example.test_6.strategies.reportStrategies.HighAmountLastMonthReportStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final Map<String, ReportStrategy> reportStrategies = new HashMap<>();

    @PostConstruct
    private void initializeStrategies() {
        reportStrategies.put("HIGH_AMOUNT_LAST_MONTH", new HighAmountLastMonthReportStrategy(reportService));
        reportStrategies.put("GROUP_BY_CURRENCY_FROM_LAST_MONTH", new GroupByCurrencyFromLastMonthReportStrategy(reportService));
        reportStrategies.put("GROUP_BY_CURRENCY_TO_LAST_MONTH", new GroupByCurrencyToLastMonthReportStrategy(reportService));
    }

    @GetMapping("/{reportName}")
    public ResponseEntity<?> generateReport(@PathVariable String reportName) {
        ReportStrategy strategy = reportStrategies.get(reportName.toUpperCase());
        if (strategy == null) {
            return new ResponseEntity<>("Invalid report name", HttpStatus.BAD_REQUEST);
        }
        return strategy.generateReport();
    }
}
