package com.example.test_6.mappings;

import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.model.transaction.command.TransactionCommand;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;

@Service
public class TransactionCommandToTransactionConverter implements Converter<TransactionCommand, Transaction> {
    @Override
    public Transaction convert(MappingContext<TransactionCommand, Transaction> mappingContext) {
        TransactionCommand command = mappingContext.getSource();
        return Transaction.builder()
                .currencyFrom(command.getCurrencyFrom())
                .currencyTo(command.getCurrencyTo())
                .baseAmount(command.getBaseAmount())
//                .convertedAmount(command.getConvertedAmount())
//                .exchangeRate(command.getExchangeRate())
//                .exchangeDate(command.getExchangeDate())
//                .transactionValueInEur(command.getTransactionValueInEur())
                .build();
    }
}
