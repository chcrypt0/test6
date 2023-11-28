package com.example.test_6.mappings;


import com.example.test_6.model.transaction.Transaction;
import com.google.gson.JsonObject;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JsonObjectToTransactionConverter implements Converter<JsonObject, Transaction> {

    @Override
    public Transaction convert(MappingContext<JsonObject, Transaction> mappingContext) {
        JsonObject jsonObject = mappingContext.getSource();
        return Transaction.builder().currencyFrom(jsonObject.get("base").getAsString())
                .currencyTo(jsonObject.get("target").getAsString())
                .baseAmount(jsonObject.get("base_amount").getAsBigDecimal())
                .convertedAmount(jsonObject.get("converted_amount").getAsBigDecimal())
                .exchangeRate(jsonObject.get("exchange_rate").getAsBigDecimal())
                .exchangeDate(LocalDateTime.now())
                .build();
    }
}
