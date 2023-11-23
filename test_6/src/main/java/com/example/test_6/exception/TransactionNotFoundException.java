package com.example.test_6.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException (String message){super(message);}
}
