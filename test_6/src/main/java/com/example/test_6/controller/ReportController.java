package com.example.test_6.controller;

import com.example.test_6.strategies.ReportStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final Map<String, ReportStrategy> reportStrategies;

    @GetMapping("/{reportName}")
    public ResponseEntity<?> generateReport(@PathVariable String reportName) {
        ReportStrategy strategy = reportStrategies.get(reportName.toUpperCase());
        if (strategy == null) {
            return new ResponseEntity<>("Invalid report name", HttpStatus.BAD_REQUEST);
        }
        return strategy.generateReport();
    }
}
