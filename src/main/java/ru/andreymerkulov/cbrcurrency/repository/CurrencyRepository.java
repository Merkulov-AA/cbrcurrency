package ru.andreymerkulov.cbrcurrency.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreymerkulov.cbrcurrency.model.Currency;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    Optional<Currency> findByCharCode(String charCode);

}
