package ru.andreymerkulov.cbrcurrency.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CbrValute {

    @JsonProperty("ID")
    private String id;
    private String numCode;
    private String charCode;
    private Integer nominal;
    private String name;
    private Double value;
    private Double previous;
}
