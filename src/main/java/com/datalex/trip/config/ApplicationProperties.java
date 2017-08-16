package com.datalex.trip.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("ndc")
@Getter
@Setter
public class ApplicationProperties {
    private String host;
    private String predictHqAuthorizationKey;
    private String predictHqUrl;
    private String airportLocatingCoordinateUrl;
    private String sitaAuthorizationKey;
    private String sitaAuthorizationValue;
    private List<String> preLoadedCities;
    private String hotelIndividualId;
    private String skyscannerApiKey;
    private String skyscannerUrlCreateSession;
    private String skyscannerUrlBase;
    private String skyscannerUrlBaseCreateDetail;
    private String skyscannerUrlBasePullDetail;
    private String skyscannerStartDatePlaceHolder;
    private String skyscannerEndDatePlaceHolder;
    private String skyscannerSessionApiPlaceHolder;
    private String skyscannerHotelIdPlaceHolder;
}
