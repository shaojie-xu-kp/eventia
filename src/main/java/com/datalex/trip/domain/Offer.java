package com.datalex.trip.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Offer {

    private List<Flight> flights = new ArrayList<>();
    private List<Hotel> hotels = new ArrayList<>();
    private List<Taxi> taxis = new ArrayList<>();
    private List<Ancillary> ancillaries= new ArrayList<>();

}
