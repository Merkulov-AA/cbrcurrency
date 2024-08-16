package ru.andreymerkulov.cbrcurrency.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andreymerkulov.cbrcurrency.model.Currency;
import ru.andreymerkulov.cbrcurrency.model.CurrencyRate;
import ru.andreymerkulov.cbrcurrency.repository.CurrencyRateRepository;
import ru.andreymerkulov.cbrcurrency.repository.CurrencyRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CurrencyRateService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyRateRepository currencyRateRepository;

    public Optional<CurrencyRate> getCurrencyRate(String charCode, LocalDate rateDate) {
        Optional<Currency> currency = currencyRepository.findByCharCode(charCode);
        if (currency.isPresent()) {
            Integer numCode = currency.get().getNumCode();
            if (rateDate != null) {
                // Приводим LocalDateTime к OffsetDateTime с использованием зоны по умолчанию
                OffsetDateTime offsetDateTime
                        = rateDate.atStartOfDay().plusDays(1)
                            .atZone(ZoneId.systemDefault())
                            .toOffsetDateTime();
                return Optional.ofNullable(
                        currencyRateRepository
                            .findTopByCurrencyNumCodeAndRateDateLessThanEqualOrderByRateDateDesc(
                                  numCode
                                , offsetDateTime
                ));
            } else {
                return Optional.ofNullable(
                        currencyRateRepository
                            .findTopByCurrencyNumCodeOrderByIdRateDateDesc(numCode)
                );
            }
        } else {
            return Optional.empty();
        }
    }
}
