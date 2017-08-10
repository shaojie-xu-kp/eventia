package com.datalex.trip.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Hotel {

    private String name;
    private String distanceToPlace;
    private String stars;
    private BigDecimal price;
    private int nights;
    private String roomStay;
    private String popularity;
    private String popularityDesc;
}
