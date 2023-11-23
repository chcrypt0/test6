package com.example.test_6.service;

import com.example.test_6.exception.CurrencyConversionException;
import com.example.test_6.exception.CurrentEurRateNotFoundException;
import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.repository.TransactionRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@Setter
@RequiredArgsConstructor
public class CalculatorService {

    private final WebClient webClient = WebClient.create();
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final RestTemplate restTemplate;

    //Uzyte api to AbstractApi Exchange Rates
    private static final String apiKey = "14e146aecbcb43f6895dc9ee49d56801";

    @Transactional
    public JsonObject convertCurrency(String currencyFrom, String currencyTo, BigInteger amount) {
        try {
            HttpResponse<String> response = Unirest.get("https://exchange-rates.abstractapi.com/v1/convert?api_key=" + apiKey
                            + "&base=" + currencyFrom + "&target=" + currencyTo + "&base_amount=" + amount)
                    .asString();

            if (response.getStatus() == 200) {
                JsonObject jsonResponse = JsonParser.parseString(response.getBody()).getAsJsonObject();

                Transaction transaction = convertJsonToTransaction(jsonResponse);

                transactionRepository.save(transaction);

                return jsonResponse;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new CurrencyConversionException("Currency conversion error: The requested operation could not be performed.");
    }

    public Transaction convertJsonToTransaction(JsonObject jsonResponse) {
        Transaction transaction = Transaction.builder().currencyFrom(jsonResponse.get("base").getAsString())
                .currencyTo(jsonResponse.get("target").getAsString())
                .baseAmount(jsonResponse.get("base_amount").getAsBigDecimal())
                .convertedAmount(jsonResponse.get("converted_amount").getAsBigDecimal())
                .exchangeRate(jsonResponse.get("exchange_rate").getAsBigDecimal())
                .exchangeDate(LocalDateTime.now())
                .build();

        Boolean isMoreThan15kEur = approveMoreThan15kEur(transaction);
        transaction.setMoreThan15kEur(isMoreThan15kEur);
        return transaction;
    }

    @Transactional
    public boolean approveMoreThan15kEur(Transaction transaction) {
        String currencyFrom = transaction.getCurrencyFrom();
        try {
            HttpResponse<String> response = Unirest.get("https://exchange-rates.abstractapi.com/v1/live?api_key=" + apiKey + "&base=" + currencyFrom)
                    .asString();

            System.out.println("Kod odpowiedzi HTTP: " + response.getStatus());
            if (response.getStatus() == 429) {
                JsonObject jsonResponse = JsonParser.parseString(response.getBody()).getAsJsonObject();

                JsonObject exchangeRates = jsonResponse.getAsJsonObject("exchange_rates");
                BigDecimal eurRate = exchangeRates.get("EUR").getAsBigDecimal();

                BigDecimal exchangeResult = transaction.getBaseAmount().multiply(eurRate);
                BigDecimal limit = new BigDecimal("15000");

                if (exchangeResult.compareTo(limit) > 0) {
                    return true;
                } else System.out.println("Odpowiedź API nie była sukcesem. Kod odpowiedzi: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
        //throw new CurrentEurRateNotFoundException("Cannot found current Euro rate");
    }

    public boolean approveMoreThan15kEur2(Transaction transaction) {
        String currencyFrom = transaction.getCurrencyFrom();
        try {
            JsonObject gsonExpectedResponse = new JsonObject();
            gsonExpectedResponse.addProperty("base", "USD");
            gsonExpectedResponse.addProperty("last_updated", 1687266900);
            JsonObject exchangeRates = new JsonObject();
            exchangeRates.addProperty("EUR", 0.915499);
            exchangeRates.addProperty("JPY", 141.801703);
            exchangeRates.addProperty("BGN", 1.790534);
            exchangeRates.addProperty("CZK", 21.755012);
            exchangeRates.addProperty("DKK", 6.818548);
            exchangeRates.addProperty("GBP", 0.785755);
            exchangeRates.addProperty("HUF", 338.64323);
            exchangeRates.addProperty("PLN", 4.058226);
            exchangeRates.addProperty("RON", 4.543166);
            exchangeRates.addProperty("SEK", 10.762886);
            exchangeRates.addProperty("CHF", 0.897464);
            exchangeRates.addProperty("ISK", 135.768562);
            exchangeRates.addProperty("NOK", 10.754372);
            exchangeRates.addProperty("HRK", 7.06591);
            exchangeRates.addProperty("RUB", 104.99999999999999);
            exchangeRates.addProperty("TRY", 23.559736);
            exchangeRates.addProperty("AUD", 1.478898);
            exchangeRates.addProperty("BRL", 4.78559);
            exchangeRates.addProperty("CAD", 1.321523);
            exchangeRates.addProperty("CNY", 7.18841);
            exchangeRates.addProperty("HKD", 7.828435);
            exchangeRates.addProperty("IDR", 14950.837682);
            exchangeRates.addProperty("ILS", 3.607434);
            exchangeRates.addProperty("INR", 82.007232);
            exchangeRates.addProperty("KRW", 1294.406299);
            exchangeRates.addProperty("MXN", 17.155818);
            exchangeRates.addProperty("MYR", 4.644969);
            exchangeRates.addProperty("NZD", 1.620159);
            exchangeRates.addProperty("PHP", 55.613842);
            exchangeRates.addProperty("SGD", 1.34377);
            exchangeRates.addProperty("THB", 34.840245);
            exchangeRates.addProperty("ZAR", 18.381672);
            exchangeRates.addProperty("ARS", 75.269373);
            exchangeRates.addProperty("DZD", 124.445887);
            exchangeRates.addProperty("MAD", 8.83269);
            exchangeRates.addProperty("TWD", 27.466513);
            exchangeRates.addProperty("BTC", 0.000039);
            exchangeRates.addProperty("ETH", 0.000571);
            exchangeRates.addProperty("BNB", 0.004132);
            exchangeRates.addProperty("DOGE", 16.348523);
            exchangeRates.addProperty("XRP", 1.92295);
            exchangeRates.addProperty("BCH", 0.009477);
            exchangeRates.addProperty("LTC", 0.012865);

            gsonExpectedResponse.add("exchange_rates", exchangeRates);


            JsonObject exchangeRates2 = gsonExpectedResponse.getAsJsonObject("exchange_rates");
            BigDecimal eurRate = exchangeRates2.get("EUR").getAsBigDecimal();

            BigDecimal exchangeResult = transaction.getBaseAmount().multiply(eurRate);
            BigDecimal limit = new BigDecimal("15000");

            if (exchangeResult.compareTo(limit) > 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
        //throw new CurrentEurRateNotFoundException("Cannot found current Euro rate");
    }

    public CompletableFuture<Transaction> convertCurrency(String currencyFrom, String currencyTo, BigInteger amount) {
        String convertUrl = "https://exchange-rates.abstractapi.com/v1/convert?api_key="
                + apiKey + "&base=" + currencyFrom + "&target=" + currencyTo + "&base_amount=" + amount;

        return webClient.get()
                .uri(convertUrl)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .toFuture()
                .thenApply(jsonResponse -> convertJsonToTransaction(jsonResponse))
                .thenCompose(transaction -> setMoreThan15kEurFlag(transaction, currencyFrom));
    }

    private Transaction convertJsonToTransaction(JsonObject jsonResponse) {
        // Konwersja JsonObject na Transaction
        // ...
        Transaction transaction = // Tworzenie obiektu Transaction z jsonResponse
        return transaction;
    }

    private CompletableFuture<Transaction> setMoreThan15kEurFlag(Transaction transaction, String currencyFrom) {
        String liveUrl = "https://exchange-rates.abstractapi.com/v1/live?api_key=" + apiKey + "&base=" + currencyFrom;

        return webClient.get()
                .uri(liveUrl)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .toFuture()
                .thenApply(liveResponse -> {
                    Boolean flag = approveMoreThan15kEur(transaction, liveResponse);
                    transaction.setMoreThan15kEur(flag);
                    return transaction;
                });
    }

}