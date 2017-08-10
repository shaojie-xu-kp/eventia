package com.datalex.trip.dto.sita;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@Data
@AllArgsConstructor
public class SitaAirportResponse {

    private String success;
    private String errorMessage;
    private List<Airport> airports;

}

