package com.datalex.trip.service;

import com.datalex.trip.config.ApplicationProperties;
import com.datalex.trip.domain.Coordinate;
import com.datalex.trip.dto.sita.Airport;
import com.datalex.trip.dto.sita.SitaAirportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirportLocatingService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebClient webClient;

    @Autowired
    ApplicationProperties applicationProperties;

    public static final String NO_AIRPORT_FOUND = "no airport found";

    private static final String SUCCESS_TRUE = "true";


    public static final String SLASH = "/";

    public List<String> localNearestAirports(Coordinate coordinate) {

        String baseUrl = applicationProperties.getAirportLocatingCoordinateUrl()
                + coordinate.getLatitude()
                + SLASH
                + coordinate.getLongitude();


        String request = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .build()
                .encode()
                .toUriString();

        HttpHeaders headers;
        headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add(applicationProperties.getSitaAuthorizationKey(), applicationProperties.getSitaAuthorizationValue());

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


    public Mono<SitaAirportResponse> localNearestAirportsAsyn(Coordinate coordinate) {

        String baseUrl = applicationProperties.getAirportLocatingCoordinateUrl()
                + coordinate.getLatitude()
                + SLASH
                + coordinate.getLongitude();

        Mono<SitaAirportResponse> sitaAirportResponseFlux = webClient
                .get()
                .uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .header(applicationProperties.getSitaAuthorizationKey(), applicationProperties.getSitaAuthorizationValue())
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(SitaAirportResponse.class))
                .log();

        return sitaAirportResponseFlux;

    }


}
