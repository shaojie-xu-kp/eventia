package com.datalex.trip.dto.indigo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Out {
    @JsonProperty(value = "operator")
    private String operator;
}
