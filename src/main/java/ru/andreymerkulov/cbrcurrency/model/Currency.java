package ru.andreymerkulov.cbrcurrency.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "currency")
@Data
public class Currency {

    @Id
    @Column(name = "num_code")
    private Integer numCode;

    private String charCode;

    private String name;
}
