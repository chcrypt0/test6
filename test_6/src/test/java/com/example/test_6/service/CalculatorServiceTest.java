package com.example.test_6.service;

import com.example.test_6.model.transaction.Transaction;
import com.example.test_6.repository.TransactionRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatorServiceTest {

    @InjectMocks
    private CalculatorService calculatorService;
    private HttpURLConnection mockConnection;


    TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUpTransactionService() {
        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionService(transactionRepository);
    }

//    @BeforeEach
//    void setUp() throws IOException {
//        transactionRepository = mock(TransactionRepository.class);
//        transactionService = new TransactionService(transactionRepository);
//
//        mockConnection = mock(HttpURLConnection.class);
//
//        //calculatorService = new CalculatorService(transactionService);
//        //calculatorService.setHttpURLConnection(mockConnection);
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("{");
//        sb.append("\"base\": \"USD\",");
//        sb.append("\"target\": \"EUR\",");
//        sb.append("\"base_amount\": 100,");
//        sb.append("\"converted_amount\": 85,");
//        sb.append("\"exchange_rate\": 0.85,");
//        sb.append("\"last_updated\": 1609459200");
//        sb.append("}");
//        String jsonResponse = sb.toString();
//
//
//        InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes(StandardCharsets.UTF_8));
//        Mockito.when(mockConnection.getInputStream()).thenReturn(inputStream);
//    }

    @Test
    void convertCurrency() throws IOException, JSONException {
        // Ustawienie oczekiwanej odpowiedzi
        JsonObject gsonExpectedResponse = new JsonObject();
        gsonExpectedResponse.addProperty("base", "USD");
        gsonExpectedResponse.addProperty("target", "EUR");
        gsonExpectedResponse.addProperty("base_amount", 100);
        gsonExpectedResponse.addProperty("converted_amount", 85);
        gsonExpectedResponse.addProperty("exchange_rate", 0.85);
        gsonExpectedResponse.addProperty("last_updated", 1609459200);

        String expectedResponseString = gsonExpectedResponse.toString();
        InputStream inputStream = new ByteArrayInputStream(expectedResponseString.getBytes(StandardCharsets.UTF_8));
        Mockito.when(mockConnection.getInputStream()).thenReturn(inputStream);

        // Wywołanie metody i sprawdzenie wyniku
        //JsonObject gsonResult = calculatorService.convertCurrency("USD", "EUR", BigInteger.valueOf(10));

        JSONObject expectedResponse = new JSONObject(gsonExpectedResponse.toString());
        JsonObject gson = calculatorService.convertCurrency("USD", "EUR", BigInteger.valueOf(100));
        JSONObject result = new JSONObject(gson.toString());


        // Asercje i weryfikacja wyniku

        JSONAssert.assertEquals(expectedResponse, result, true);
    }

    @Test
    void parseConversionResponse() throws JSONException {
        //given
        Gson gson = new Gson();
        JsonObject gsonObjectToParse = new JsonObject();
        gsonObjectToParse.addProperty("base", "USD");
        gsonObjectToParse.addProperty("target", "EUR");
        gsonObjectToParse.addProperty("base_amount", 100);
        gsonObjectToParse.addProperty("converted_amount", 85);
        gsonObjectToParse.addProperty("exchange_rate", 0.85);
        gsonObjectToParse.addProperty("last_updated", 1609459200);

        Transaction transactionToSave = new Transaction();

        //when

        transactionToSave = Transaction.builder().currencyFrom(gsonObjectToParse.get("base").getAsString())
                .currencyTo(gsonObjectToParse.get("target").getAsString())
                .baseAmount(gsonObjectToParse.get("base_amount").getAsBigDecimal())
                .convertedAmount(gsonObjectToParse.get("converted_amount").getAsBigDecimal())
                .exchangeRate(gsonObjectToParse.get("exchange_rate").getAsBigDecimal())
                .build();
        String transactiontString = gson.toJson(transactionToSave);
        JsonObject gsonTransaction = gson.fromJson(transactiontString, JsonObject.class);

        JSONObject jsonObjectToParse = new JSONObject(gsonObjectToParse.toString());
        JSONObject jsonObjectTransaction = new JSONObject(gsonTransaction.toString());

        //then
    }

    @Test
    void approveMoreThan15kEur() {
        //given
        Transaction transaction = Transaction.builder().currencyFrom("USD")
                .currencyTo("EUR")
                .baseAmount(BigDecimal.valueOf(18888))
                .convertedAmount(BigDecimal.valueOf(18309.98))
                .exchangeRate(BigDecimal.valueOf(0.915499))
                .exchangeDate(LocalDateTime.now())
                .build();
        //when

        boolean result = calculatorService.approveMoreThan15kEur2(transaction);

        //then

        assertTrue(result);

    }

    @Test
    void approveMoreThan15kEur2() {

        Transaction transaction = Transaction.builder().currencyFrom("USD")
                .currencyTo("EUR")
                .baseAmount(BigDecimal.valueOf(18888))
                .convertedAmount(BigDecimal.valueOf(18309.98))
                .exchangeRate(BigDecimal.valueOf(0.915499))
                .exchangeDate(LocalDateTime.now())
                .build();

        String apiKey = "14e146aecbcb43f6895dc9ee49d56801";

        Boolean result = null;

        String currencyFrom = transaction.getCurrencyFrom();
        try {
            HttpResponse<String> response = Unirest.get("https://exchange-rates.abstractapi.com/v1/live?api_key=" + apiKey + "&base=" + currencyFrom + "&target=EUR")
                    .asString();
            if (response.getStatus() == 200) {
                JsonObject jsonResponse = JsonParser.parseString(response.getBody()).getAsJsonObject();

                JsonObject exchangeRates = jsonResponse.getAsJsonObject("exchange_rates");
                BigDecimal eurRate = exchangeRates.get("EUR").getAsBigDecimal();

                BigDecimal exchangeResult = transaction.getBaseAmount().multiply(eurRate);
                BigDecimal limit = new BigDecimal("15000");

                if (exchangeResult.compareTo(limit) > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result = false;
        assertTrue(result);

    }
}