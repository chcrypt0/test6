package com.example.test_6.controller;

import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.model.transaction.command.TransactionCommand;
import com.example.test_6.model.transaction.dto.TransactionDto;
import com.example.test_6.model.transaction.dto.TransactionPageDto;
import com.example.test_6.model.transaction.dto.TransactionResponseDto;
import com.example.test_6.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/calculator")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    private final ModelMapper modelMapper;
    @GetMapping("/exchange")
    public ResponseEntity<TransactionResponseDto> convertCurrency(@RequestBody TransactionCommand command) {
        Transaction transaction = transactionService.convertCurrency(command);

        return new ResponseEntity<>(modelMapper.map(transaction, TransactionResponseDto.class), HttpStatus.OK);
    }

    @GetMapping("/exchanges")
    public ResponseEntity<List<TransactionPageDto>> getTransactionsHistory(@PageableDefault(size = 15)Pageable pageable) {
        Page<Transaction> pageResult = transactionService.findPaginated(pageable);
        List<TransactionPageDto> dtos = pageResult.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionPageDto.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> findById(@PathVariable("id") Long id){
        TransactionDto transactionDto = transactionService.findById(id);
        return new ResponseEntity<>(transactionDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> findAll(){
        List<TransactionDto> transactionDtos = transactionService.findAll();
        return new ResponseEntity<>(transactionDtos, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id){
        transactionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> edit(@PathVariable("id") Long id, @RequestBody TransactionCommand command){
        TransactionDto transactionDto = transactionService.edit(id, command);
        return new ResponseEntity<>(transactionDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TransactionDto> editPartially(@PathVariable("id") Long id, @RequestBody TransactionCommand command){
        TransactionDto transactionDto = transactionService.editPartially(id, command);
        return new ResponseEntity<>(transactionDto, HttpStatus.OK);
    }
}
