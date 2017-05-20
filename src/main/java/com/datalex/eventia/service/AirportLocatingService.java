package com.datalex.eventia.service;

import com.datalex.eventia.ApplicationProperties;
import com.datalex.eventia.domain.Coordinate;
import com.datalex.eventia.dto.sita.SitaAirportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.Arrays;


/**
 * Created by shaojie.xu on 19/05/2017.
 */
@Service
public class AirportLocatingService {

        @Autowired
        RestTemplate restTemplate;

        @Autowired
        ApplicationProperties applicationProperties;

        public static final String NO_AIRPORT_FOUND = "no airport found";

        private static final String SUCCESS_TRUE = "true";

        private HttpHeaders headers;

        @PostConstruct
        private void init(){
                headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                headers.add(applicationProperties.getSitaAuthorizationKey(), applicationProperties.getSitaAuthorizationValue());
        }

        public static final String SLASH = "/";

        public String localNearestAirport(Coordinate coordinate){

                String request = UriComponentsBuilder
                        .fromHttpUrl(applicationProperties.getAirportLocatingCoordinateUrl()
                                                        + coordinate.getLatitude()
                                                        + SLASH
                                                        + coordinate.getLongitude())
                        .build()
                        .encode()
                        .toUriString();
                HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
                ResponseEntity<SitaAirportResponse> responseEntity = restTemplate.exchange(request, HttpMethod.GET, entity, SitaAirportResponse.class);
                if(responseEntity.hasBody())
                {
                        SitaAirportResponse response = responseEntity.getBody();
                        if(SUCCESS_TRUE.equals(response.getSuccess()))
                        {
                                return  response.getAirports().stream()
                                        .map(airport -> airport.getIatacode())
                                        .findAny()
                                        .orElse(NO_AIRPORT_FOUND);
                        }
                }

                return NO_AIRPORT_FOUND;

        }


}
