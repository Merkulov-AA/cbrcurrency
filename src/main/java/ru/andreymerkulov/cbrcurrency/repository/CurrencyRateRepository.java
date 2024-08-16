package ru.andreymerkulov.cbrcurrency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.andreymerkulov.cbrcurrency.model.CurrencyRate;
import ru.andreymerkulov.cbrcurrency.model.CurrencyRateId;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, CurrencyRateId> {
    @Query("SELECT cr " +
            "FROM CurrencyRate cr " +
            "WHERE cr.currency.numCode = :numCode " +
            "  AND cr.id.rateDate <= :rateDate " +
            "ORDER BY cr.id.rateDate DESC " +
            "LIMIT 1")
    CurrencyRate findTopByCurrencyNumCodeAndRateDateLessThanEqualOrderByRateDateDesc(
              @Param("numCode") Integer numCode
            , @Param("rateDate") OffsetDateTime rateDate);

    CurrencyRate findTopByCurrencyNumCodeOrderByIdRateDateDesc(Integer numCode);
}
