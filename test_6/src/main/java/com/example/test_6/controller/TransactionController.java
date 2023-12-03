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
    @PostMapping("/exchange")
    public ResponseEntity<TransactionResponseDto> convertCurrency(@RequestBody TransactionCommand command) {
        return new ResponseEntity<>(transactionService.convertCurrency(command), HttpStatus.OK);
    }

    @GetMapping("/exchanges")
    public ResponseEntity<List<TransactionPageDto>> getTransactionsHistory(@PageableDefault(size = 15)Pageable pageable) {
        return new ResponseEntity<>(transactionService.findPaginated(pageable), HttpStatus.OK);
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

}
