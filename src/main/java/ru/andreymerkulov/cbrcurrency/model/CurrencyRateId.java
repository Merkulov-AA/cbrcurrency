package ru.andreymerkulov.cbrcurrency.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Embeddable
@Data
public class CurrencyRateId implements Serializable {

    @Column(name = "num_code")
    private Integer numCode;

    @Column(name = "rate_date")
    private OffsetDateTime rateDate;
}