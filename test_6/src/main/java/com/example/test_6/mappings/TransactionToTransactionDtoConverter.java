package com.example.test_6.mappings;

import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.model.transaction.dto.TransactionDto;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;

@Service
public class TransactionToTransactionDtoConverter implements Converter<Transaction, TransactionDto> {
    @Override
    public TransactionDto convert(MappingContext<Transaction, TransactionDto> mappingContext) {
        Transaction transaction = mappingContext.getSource();
        return TransactionDto.builder()
                .currencyFrom(transaction.getCurrencyFrom())
                .currencyTo(transaction.getCurrencyTo())
                .baseAmount(transaction.getBaseAmount())
                .convertedAmount(transaction.getConvertedAmount())
                .exchangeRate(transaction.getExchangeRate())
                .exchangeDate(transaction.getExchangeDate())
                .transactionValueInEur(transaction.getTransactionValueInEur())
                .build();
    }
}
