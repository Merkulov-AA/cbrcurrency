package ru.andreymerkulov.cbrcurrency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreymerkulov.cbrcurrency.model.CurrencyRate;
import ru.andreymerkulov.cbrcurrency.model.CurrencyRateId;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, CurrencyRateId> {
}
