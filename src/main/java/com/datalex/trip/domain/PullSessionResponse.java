package com.datalex.trip.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PullSessionResponse {

    @JsonProperty("hotels_prices")
    private List<HotelPriceInfo> hotelsPrices;

    private String status;

    private List<HotelInfo> hotels;

}
