package ru.andreymerkulov.cbrcurrency.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.andreymerkulov.cbrcurrency.dto.CbrValute;
import ru.andreymerkulov.cbrcurrency.model.Currency;

@Mapper(componentModel = "spring"
        , unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CurrencyMapper {

    Currency cbrValuteToCurrency(CbrValute valute);

}
