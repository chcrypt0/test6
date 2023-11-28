package com.example.test_6.strategies.reportStrategies;

import com.example.test_6.service.ReportService;
import com.example.test_6.strategies.ReportStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RequiredArgsConstructor
public class GroupByCurrencyFromLastMonthReportStrategy implements ReportStrategy {
    private final ReportService reportService;

    @Override
    public ResponseEntity<?> generateReport() {
        Map<String, Integer> groupByCurrencyFrom = reportService.getGroupByCurrencyFromLastMonth();
        return new ResponseEntity<>(groupByCurrencyFrom, HttpStatus.OK);
    }
}

