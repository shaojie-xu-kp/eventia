package com.datalex.trip.dto.sita;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Setter
@Getter
@AllArgsConstructor
public class Airport{

        private String name;
        private String city;
        private String country;
        private String lat;
        private String lng;
        private String iatacode;

}
