package ru.andreymerkulov.cbrcurrency.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.andreymerkulov.cbrcurrency.dto.CbrResponse;
import ru.andreymerkulov.cbrcurrency.dto.CbrValute;
import ru.andreymerkulov.cbrcurrency.model.Currency;
import ru.andreymerkulov.cbrcurrency.model.CurrencyRate;

@Mapper(componentModel = "spring")
public interface CurrencyRateMapper {

    @Mapping(target = "id.numCode", source = "currency.numCode")
    @Mapping(target = "id.rateDate", source = "cbrResponse.date")
    @Mapping(target = "value", source = "valute.value")
    CurrencyRate toCurrencyRate(
            CbrResponse cbrResponse
            , Currency currency
            , CbrValute valute);
}
