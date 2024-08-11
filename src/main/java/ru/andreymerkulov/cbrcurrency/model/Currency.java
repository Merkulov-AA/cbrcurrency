package ru.andreymerkulov.cbrcurrency.model;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Table(name = "currency")
@Data
public class Currency {

    @Id
    @Column(name = "num_code")
    private Integer numCode;

    @Column(name = "char_code")
    private String charCode;

    private String name;
}
