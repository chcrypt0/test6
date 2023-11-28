package com.example.test_6.exception;

public class InvalidExchangeRateException extends RuntimeException {
    public InvalidExchangeRateException(String message) {
        super(message);
    }
}

