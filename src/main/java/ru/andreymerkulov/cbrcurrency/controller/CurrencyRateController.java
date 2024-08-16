package ru.andreymerkulov.cbrcurrency.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.andreymerkulov.cbrcurrency.model.CurrencyRate;
import ru.andreymerkulov.cbrcurrency.service.CurrencyRateService;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class CurrencyRateController {

    private CurrencyRateService currencyRateService;

    @GetMapping("/rates/{charCode}")
    public ResponseEntity<CurrencyRate> getCurrencyRate(
            @PathVariable
            String charCode,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate rateDate)
    {
        Optional<CurrencyRate> rate = currencyRateService.getCurrencyRate(charCode, rateDate);
        return rate.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
