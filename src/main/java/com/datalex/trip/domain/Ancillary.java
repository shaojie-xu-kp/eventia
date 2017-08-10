package com.datalex.trip.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Ancillary {

    private String description;
    private String price;

}
