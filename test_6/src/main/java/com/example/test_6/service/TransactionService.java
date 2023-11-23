package com.example.test_6.service;

import com.example.test_6.exception.TransactionNotFoundException;
import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.model.transaction.command.TransactionCommand;
import com.example.test_6.model.transaction.dto.TransactionDto;
import com.example.test_6.model.transaction.dto.TransactionPageDto;
import com.example.test_6.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;


    public Transaction add(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction findById(long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(String.format("Transaction with id: %s not found", id)));
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public void removeTransaction(long id) {
        transactionRepository.deleteById(id);
    }

    public Page<Transaction> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return transactionRepository.findAll(pageable);
    }


    @Transactional
    public Transaction edit(Long id, TransactionCommand command) {
        return transactionRepository.findById(id)
                .map(transactionToEdit -> {
                    transactionToEdit.setCurrencyFrom(command.getCurrencyFrom());
                    transactionToEdit.setCurrencyTo(command.getCurrencyTo());
                    transactionToEdit.setBaseAmount(command.getBaseAmount());
                    transactionToEdit.setConvertedAmount(command.getConvertedAmount());
                    transactionToEdit.setExchangeRate(command.getExchangeRate());
                    transactionToEdit.setExchangeDate(command.getExchangeDate());
                    return transactionToEdit;
                }).orElseThrow(() -> new TransactionNotFoundException(String.format("Transaction with id: %s not found", id)));
    }

    @Transactional
    public Transaction editPartially(Long id, TransactionCommand command) {
        return transactionRepository.findById(id)
                .map(transactionToEdit -> {
                    Optional.ofNullable(command.getCurrencyFrom()).ifPresent(transactionToEdit::setCurrencyFrom);
                    Optional.ofNullable(command.getCurrencyTo()).ifPresent(transactionToEdit::setCurrencyTo);
                    Optional.ofNullable(command.getBaseAmount()).ifPresent(transactionToEdit::setBaseAmount);
                    Optional.ofNullable(command.getConvertedAmount()).ifPresent(transactionToEdit::setConvertedAmount);
                    Optional.ofNullable(command.getExchangeRate()).ifPresent(transactionToEdit::setExchangeRate);
                    Optional.ofNullable(command.getExchangeDate()).ifPresent(transactionToEdit::setExchangeDate);
                    return transactionToEdit;
                }).orElseThrow(() -> new TransactionNotFoundException(String.format("Transaction with id: %s not found", id)));
    }

}
