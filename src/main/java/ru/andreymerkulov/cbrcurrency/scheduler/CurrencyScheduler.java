package ru.andreymerkulov.cbrcurrency.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.andreymerkulov.cbrcurrency.service.CurrencyService;

@Component
@RequiredArgsConstructor
public class CurrencyScheduler {

    private final CurrencyService currencyService;

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void fetchCurrencyData() {
        currencyService.run();
    }
}
