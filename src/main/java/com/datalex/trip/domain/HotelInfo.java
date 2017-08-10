package com.datalex.trip.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by shaojie.xu on 21/05/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@Data
public class HotelInfo {

    private String name;
    private String hotel_id;
    private String popularity;
    private String popularity_desc;
    private String star_rating;

}
