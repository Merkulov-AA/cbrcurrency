package ru.andreymerkulov.cbrcurrency.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.andreymerkulov.cbrcurrency.service.CurrencyService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CurrencyScheduler {

    private final CurrencyService currencyService;
    @Retryable(
            value = {IOException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 60000, multiplier = 3))
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void fetchCurrencyData() throws IOException {
        currencyService.run();
    }
}
