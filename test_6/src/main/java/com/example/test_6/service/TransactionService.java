package com.example.test_6.service;

import com.example.test_6.exception.*;
import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.model.transaction.command.TransactionCommand;
import com.example.test_6.model.transaction.dto.TransactionDto;
import com.example.test_6.model.transaction.dto.TransactionPageDto;
import com.example.test_6.model.transaction.dto.TransactionResponseDto;
import com.example.test_6.repository.TransactionRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final Environment environment;
    private static final String apiKey = "14e146aecbcb43f6895dc9ee49d56801";

    @Transactional
    public TransactionResponseDto convertCurrency(TransactionCommand command) {
        try {
            JsonObject exchangeRates = fetchExchangeRates(command.getCurrencyFrom().toUpperCase(), command.getCurrencyTo().toUpperCase());
            Transaction transaction = createTransaction(command, exchangeRates);
            transactionRepository.save(transaction);
            return modelMapper.map(transaction, TransactionResponseDto.class);

        } catch (Exception e) {
            throw new CurrencyConversionException("Unexpected error during currency conversion.");
        }
    }
    public JsonObject fetchExchangeRates(String currencyFrom, String currencyTo) throws CurrencyConversionException {
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return getMockResponse(currencyFrom, currencyTo);
        } else {
            return apiRequest(currencyFrom, currencyTo);
        }
    }

    public JsonObject getMockResponse(String currencyFrom, String currencyTo) {
        try {
            String exampleResponse = "{\"base\":\"" +
                    currencyFrom +
                    "\",\"last_updated\":1701177300,\"exchange_rates\":{\"EUR\":10,\"" +
                    currencyTo +
                    "\":10}}";
            JsonObject jsonResponse = JsonParser.parseString(exampleResponse).getAsJsonObject();
            return Optional.ofNullable(jsonResponse.getAsJsonObject("exchange_rates"))
                    .orElseThrow(() -> new ExchangeRateFetchException("Failed to fetch exchange rates from the API"));

        } catch (Exception e) {
            throw new CurrencyConversionException("Error during currency conversion.");
        }
    }

    private JsonObject apiRequest(String currencyFrom, String currencyTo) throws CurrencyConversionException {
        try {
            HttpResponse<String> response = Unirest.get("https://exchange-rates.abstractapi.com/v1/live?api_key=" + apiKey + "&base=" + currencyFrom + "&target=" + currencyTo + ",EUR")
                    .asString();

            if (response.getStatus() == 200) {
                JsonObject jsonResponse = JsonParser.parseString(response.getBody()).getAsJsonObject();
                return Optional.ofNullable(jsonResponse.getAsJsonObject("exchange_rates"))
                        .orElseThrow(() -> new ExchangeRateFetchException("Failed to fetch exchange rates from the API. Status code: " + response.getStatus()));
            } else {
                throw new BadExpectedStatusException("Incorrect response status from API");
            }
        } catch (Exception e) {
            throw new CurrencyConversionException("Error during currency conversion.");
        }
    }
    private Transaction createTransaction(TransactionCommand command, JsonObject exchangeRates) {

        BigDecimal eurRate = Optional.ofNullable(exchangeRates.get("EUR"))
                .map(JsonElement::getAsBigDecimal)
                .orElseThrow(() -> new InvalidExchangeRateException("Exchange rate EUR is null"));

        BigDecimal toRate = Optional.ofNullable(exchangeRates.get(command.getCurrencyTo()))
                .map(JsonElement::getAsBigDecimal)
                .orElseThrow(() -> new InvalidExchangeRateException("Expected exchange rate is null"));

        Transaction transaction = modelMapper.map(command, Transaction.class);
        BigDecimal baseAmount = command.getBaseAmount();

        transaction.setConvertedAmount(baseAmount.multiply(toRate));

        transaction.setExchangeRate(toRate);
        transaction.setExchangeDate(LocalDateTime.now());
        transaction.setTransactionValueInEur(baseAmount.multiply(eurRate));

        return transaction;
    }

    public TransactionDto findById(long id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(
                () -> new TransactionNotFoundException(String.format("Transaction with id: %s not found", id)));
        return modelMapper.map(transaction, TransactionDto.class);
    }

    public List<TransactionDto> findAll() {
        return transactionRepository.findAll().stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class))
                .toList();
    }

    public void deleteById(long id) {
        transactionRepository.deleteById(id);
    }

    public List<TransactionPageDto> findPaginated(Pageable pageable) {
        Page<Transaction> pageResult = transactionRepository.findAll(pageable);
        return pageResult.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionPageDto.class))
                .collect(Collectors.toList());
    }
}
