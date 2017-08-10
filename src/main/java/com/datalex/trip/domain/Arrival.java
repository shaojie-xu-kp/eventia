package com.datalex.trip.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class Arrival {
    private String airportCode;
    private String date;
    private String time;
}
