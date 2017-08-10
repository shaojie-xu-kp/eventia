package com.datalex.trip.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OriginDestination {
    private Departure departure;
    private Arrival arrival;
    private String flightNumber;
}
