package com.example.test_6.strategies.reportStrategies;

import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.service.ReportService;
import com.example.test_6.strategies.ReportStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service("HIGH_AMOUNT_LAST_MONTH")
public class HighAmountLastMonthReportStrategy implements ReportStrategy {
    private final ReportService reportService;
    @Override
    public ResponseEntity<?> generateReport() {
        List<Transaction> highAmountExchanges = reportService.getHighAmountLastMonth();
        return new ResponseEntity<>(highAmountExchanges, HttpStatus.OK);
    }
}
