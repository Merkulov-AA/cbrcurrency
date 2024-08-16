package ru.andreymerkulov.cbrcurrency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.andreymerkulov.cbrcurrency.dto.CbrResponse;
import ru.andreymerkulov.cbrcurrency.dto.CbrValute;
import ru.andreymerkulov.cbrcurrency.mapper.CurrencyMapper;
import ru.andreymerkulov.cbrcurrency.model.Currency;
import ru.andreymerkulov.cbrcurrency.model.CurrencyRate;
import ru.andreymerkulov.cbrcurrency.model.CurrencyRateId;
import ru.andreymerkulov.cbrcurrency.repository.CurrencyRateRepository;
import ru.andreymerkulov.cbrcurrency.repository.CurrencyRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private final CurrencyRepository currencyRepository;
    private final CurrencyRateRepository currencyRateRepository;

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

    public void saveCurrencyData(CbrResponse cbrResponse) {
        for (Map.Entry<String, CbrValute> entry : cbrResponse.getValute().entrySet()) {
            CbrValute valute = entry.getValue();

            Currency currency = currencyRepository.findById(valute.getNumCode())
                    .orElseGet(() -> {
                        Currency newCurrency = currencyMapper.cbrValuteToCurrency(valute);
                        return currencyRepository.save(newCurrency);
                    });

            CurrencyRateId currencyRateId = new CurrencyRateId();
            currencyRateId.setNumCode(currency.getNumCode());
            currencyRateId.setRateDate(cbrResponse.getDate());

            CurrencyRate currencyRate = new CurrencyRate();
            currencyRate.setId(currencyRateId);
            currencyRate.setCurrency(currency);
            currencyRate.setValue(BigDecimal.valueOf(valute.getValue()));

            currencyRateRepository.save(currencyRate);
        }
    }
}
