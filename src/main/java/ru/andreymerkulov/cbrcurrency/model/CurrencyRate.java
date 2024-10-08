package ru.andreymerkulov.cbrcurrency.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "currency_rate")
@Data
public class CurrencyRate implements Serializable {

    @EmbeddedId
    private CurrencyRateId id;

    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name = "num_code", referencedColumnName = "num_code", insertable = false, updatable = false)
    private Currency currency;
}
