package com.example.test_6.service;

import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionRepository);
    }

    @Test
    void add() {
    }

    @Test
    void findById() {
    }

    @Test
    void findAll() {
    }

    @Test
    void removeTransaction() {
    }

    @Test
    void findPaginated() {
        int pageNo1 = 0;
        int pageNo2 = 1;
        int pageSize = 2;

        Transaction transaction1 = new Transaction(/* inicjalizacja */);
        Transaction transaction2 = new Transaction(/* inicjalizacja */);
        Transaction transaction3 = new Transaction(/* inicjalizacja */);
        Transaction transaction4 = new Transaction(/* inicjalizacja */);
        Transaction transaction5 = new Transaction(/* inicjalizacja */);

        List<Transaction> allTransactions = Arrays.asList(transaction1, transaction2, transaction3, transaction4, transaction5);
        Page<Transaction> expectedPage1 = new PageImpl<>(allTransactions.subList(0, 2), PageRequest.of(pageNo1, pageSize), allTransactions.size());
        Page<Transaction> expectedPage2 = new PageImpl<>(allTransactions.subList(2, 4), PageRequest.of(pageNo1, pageSize), allTransactions.size());


        Mockito.when(transactionRepository.findAll(PageRequest.of(pageNo1, pageSize))).thenReturn(expectedPage1);
        Mockito.when(transactionRepository.findAll(PageRequest.of(pageNo2, pageSize))).thenReturn(expectedPage2);

        Page<Transaction> resultPage1 = transactionService.findPaginated(pageNo1, pageSize);
        Page<Transaction> resultPage2 = transactionService.findPaginated(pageNo2, pageSize);

        assertEquals(expectedPage1, resultPage1);
        assertEquals(2, resultPage1.getContent().size());
        assertEquals(expectedPage2, resultPage2);
        assertEquals(2, resultPage2.getContent().size());
    }

    @Test
    void edit() {
    }

    @Test
    void editPartially() {
    }
}