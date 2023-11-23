package com.example.test_6.controller;

import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.model.transaction.dto.TransactionPageDto;
import com.example.test_6.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    private final ModelMapper modelMapper;

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionPageDto>> getAllTransactions(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "15") int size) {
        Page<Transaction> pageResult = transactionService.findPaginated(page, size);
        List<TransactionPageDto> dtos = pageResult.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionPageDto.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

}
