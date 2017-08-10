package com.datalex.trip.dto.indigo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class IndigoResponse {
    @JsonProperty(value = "results")
    List<Result> results;

}
