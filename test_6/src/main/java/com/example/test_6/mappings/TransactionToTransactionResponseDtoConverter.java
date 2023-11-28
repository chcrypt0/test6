package com.example.test_6.mappings;

import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.model.transaction.dto.TransactionResponseDto;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;

@Service
public class TransactionToTransactionResponseDtoConverter implements Converter<Transaction, TransactionResponseDto> {
    @Override
    public TransactionResponseDto convert(MappingContext<Transaction, TransactionResponseDto> mappingContext) {
        Transaction transaction = mappingContext.getSource();
        return TransactionResponseDto.builder()
                .convertedAmount(transaction.getConvertedAmount())
                .build();
    }
}
