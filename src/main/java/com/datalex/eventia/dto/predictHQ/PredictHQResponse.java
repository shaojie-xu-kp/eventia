package com.datalex.eventia.dto.predictHQ;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PredictHQResponse {
    private List<Event> results;

}
