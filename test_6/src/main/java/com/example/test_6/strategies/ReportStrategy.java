package com.example.test_6.strategies;

import org.springframework.http.ResponseEntity;

public interface ReportStrategy {
    ResponseEntity<?> generateReport();
}
