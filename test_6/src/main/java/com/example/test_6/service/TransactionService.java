package com.example.test_6.service;

import com.example.test_6.exception.*;
import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.model.transaction.command.TransactionCommand;
import com.example.test_6.model.transaction.dto.TransactionDto;
import com.example.test_6.repository.TransactionRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private static final String apiKey = "14e146aecbcb43f6895dc9ee49d56801";

    @Transactional
    public Transaction convertCurrency(TransactionCommand command) {
        try {
            JsonObject exchangeRates = fetchExchangeRates(command.getCurrencyFrom().toUpperCase(), command.getCurrencyTo().toUpperCase());

            Transaction transaction = createTransaction(command, exchangeRates);
            transactionRepository.save(transaction);
            return transaction;

        } catch (Exception e) {
            throw new CurrencyConversionException("Unexpected error during currency conversion.");
        }
    }

    private JsonObject fetchExchangeRates(String currencyFrom, String currencyTo) throws CurrencyConversionException {
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
            throw new CurrencyConversionException("Unexpected error during currency conversion.");
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
                .map(garage -> modelMapper.map(garage, TransactionDto.class))
                .toList();
    }

    public void deleteById(long id) {
        transactionRepository.deleteById(id);
    }

    public Page<Transaction> findPaginated(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Transactional
    public TransactionDto edit(Long id, TransactionCommand command) {
        Transaction transactionToEdit = modelMapper.map(findById(id), Transaction.class);

        transactionToEdit.setCurrencyFrom(command.getCurrencyFrom());
        transactionToEdit.setCurrencyTo(command.getCurrencyTo());
        transactionToEdit.setBaseAmount(command.getBaseAmount());
        transactionToEdit.setConvertedAmount(command.getConvertedAmount());
        transactionToEdit.setExchangeRate(command.getExchangeRate());
        transactionToEdit.setExchangeDate(command.getExchangeDate());

        transactionRepository.save(transactionToEdit);
        return modelMapper.map(transactionToEdit, TransactionDto.class);
    }


    @Transactional
    public TransactionDto editPartially(Long id, TransactionCommand command) {
        Transaction transactionToEdit = modelMapper.map(findById(id), Transaction.class);

        Optional.ofNullable(command.getCurrencyFrom()).ifPresent(transactionToEdit::setCurrencyFrom);
        Optional.ofNullable(command.getCurrencyTo()).ifPresent(transactionToEdit::setCurrencyTo);
        Optional.ofNullable(command.getBaseAmount()).ifPresent(transactionToEdit::setBaseAmount);
        Optional.ofNullable(command.getConvertedAmount()).ifPresent(transactionToEdit::setConvertedAmount);
        Optional.ofNullable(command.getExchangeRate()).ifPresent(transactionToEdit::setExchangeRate);
        Optional.ofNullable(command.getExchangeDate()).ifPresent(transactionToEdit::setExchangeDate);

        transactionRepository.save(transactionToEdit);
        return modelMapper.map(transactionToEdit, TransactionDto.class);
    }
}
