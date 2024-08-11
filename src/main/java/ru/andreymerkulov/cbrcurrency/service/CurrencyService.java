package ru.andreymerkulov.cbrcurrency.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.andreymerkulov.cbrcurrency.dto.CbrResponse;
import ru.andreymerkulov.cbrcurrency.dto.CbrValute;
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

    private final CurrencyRepository currencyRepository;
    private final CurrencyRateRepository currencyRateRepository;

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void fetchAndSaveCurrencyData() {
        try {
            String response = restTemplate.getForObject(cbrApiUrl, String.class);
            CbrResponse cbrResponse = objectMapper.readValue(response, CbrResponse.class);
            log.info("cbrResponse" + cbrResponse.toString());

            for (Map.Entry<String, CbrValute> entry : cbrResponse.getValute().entrySet()) {
                CbrValute valute = entry.getValue();

                Currency currency = currencyRepository.findById(Integer.valueOf(valute.getNumCode()))
                        .orElseGet(() -> {
                            Currency newCurrency = new Currency();
                            newCurrency.setNumCode(Integer.valueOf(valute.getNumCode()));
                            newCurrency.setCharCode(valute.getCharCode());
                            newCurrency.setName(valute.getName());
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

            log.info("Currency data fetched and saved successfully.");
        } catch (IOException e) {
            log.error("Error fetching or parsing currency data: {}", e.getMessage());
        }
    }

    private LocalDateTime parseDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        String formattedDate = date.format(formatter);
        return LocalDateTime.parse(formattedDate, formatter);
    }
}
