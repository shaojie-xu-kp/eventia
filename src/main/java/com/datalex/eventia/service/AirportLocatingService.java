package com.datalex.eventia.service;

import com.datalex.eventia.ApplicationProperties;
import com.datalex.eventia.domain.Coordinate;
import com.datalex.eventia.dto.sita.Airport;
import com.datalex.eventia.dto.sita.SitaAirportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    private void init() {
        headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add(applicationProperties.getSitaAuthorizationKey(), applicationProperties.getSitaAuthorizationValue());
    }

    public static final String SLASH = "/";

    public List<String> localNearestAirports(Coordinate coordinate) {

        String request = UriComponentsBuilder
                .fromHttpUrl(applicationProperties.getAirportLocatingCoordinateUrl()
                        + coordinate.getLatitude()
                        + SLASH
                        + coordinate.getLongitude())
                .queryParam("maxAirports", 10)
                .build()
                .encode()
                .toUriString();

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<SitaAirportResponse> responseEntity = restTemplate.exchange(request, HttpMethod.GET, entity, SitaAirportResponse.class);

        if (responseEntity.hasBody()) {
            SitaAirportResponse response = responseEntity.getBody();
            if (SUCCESS_TRUE.equals(response.getSuccess())) {
                return response.getAirports().stream()
                        .map(Airport::getIatacode)
                        .collect(Collectors.toList());
            }
        }
        return Collections.singletonList(NO_AIRPORT_FOUND);

    }


}
