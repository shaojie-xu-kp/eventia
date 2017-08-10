package com.datalex.trip.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Taxi {

    private String pickupTime;
    private BigDecimal price;
    private String flightId;
    private String operator;

}
