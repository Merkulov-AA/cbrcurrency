package ru.andreymerkulov.cbrcurrency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.andreymerkulov.cbrcurrency.dto.CbrResponse;
import ru.andreymerkulov.cbrcurrency.dto.CbrValute;
import ru.andreymerkulov.cbrcurrency.mapper.CurrencyMapper;
import ru.andreymerkulov.cbrcurrency.mapper.CurrencyRateMapper;
import ru.andreymerkulov.cbrcurrency.model.Currency;
import ru.andreymerkulov.cbrcurrency.model.CurrencyRate;
import ru.andreymerkulov.cbrcurrency.repository.CurrencyRateRepository;
import ru.andreymerkulov.cbrcurrency.repository.CurrencyRepository;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
//@AllArgsConstructor
@RequiredArgsConstructor
public class CurrencyService {

    @Value("${cbr.api-url}")
    private String cbrApiUrl;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final CurrencyMapper currencyMapper;
    private final CurrencyRateMapper currencyRateMapper;

    private final CurrencyRepository currencyRepository;
    private final CurrencyRateRepository currencyRateRepository;

    @Retryable(
            value = {IOException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 60000, multiplier = 3)) // delay = 1 minute
    public void run() throws IOException {
        try {
            CbrResponse cbrResponse = fetch();
            saveCurrencyData(cbrResponse);

            log.info("Currency data fetched and saved successfully.");
        } catch (IOException e) {
            log.error("Error fetching or parsing currency data: {}", e.getMessage());
            throw e;
        }
    }

    public CbrResponse fetch() throws JsonProcessingException {
        String response = restTemplate.getForObject(cbrApiUrl, String.class);
        return objectMapper.readValue(response, CbrResponse.class);
    }

    @Transactional
    public void saveCurrencyData(CbrResponse cbrResponse) {
        for (Map.Entry<String, CbrValute> entry : cbrResponse.getValute().entrySet()) {
            CbrValute valute = entry.getValue();

            Currency currency = currencyRepository.findById(valute.getNumCode())
                    .orElseGet(() -> {
                        Currency newCurrency = currencyMapper.cbrValuteToCurrency(valute);
                        return currencyRepository.save(newCurrency);
                    });

            CurrencyRate currencyRate = currencyRateMapper.toCurrencyRate(cbrResponse, currency, valute);

            currencyRateRepository.save(currencyRate);
        }
    }
}
