package com.datalex.trip.dto.indigo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Result {
    @JsonProperty(value = "cost_total")
    private String costTotal;
    @JsonProperty(value = "out")
    private Out out;
}
