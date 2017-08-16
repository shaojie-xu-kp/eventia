package com.datalex.trip.service;

import com.datalex.trip.domain.Coordinate;
import com.datalex.trip.dto.sita.SitaAirportResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Created by shaojie.xu on 11/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SitaServiceTest {

    @Autowired
    private AirportLocatingService airportLocatingService;

    @Autowired
    WebClient webClient;

    private static final Coordinate coordinate = new Coordinate("-6.219504","53.435589");

    @Test
    public void testLocatingAirport() {


        List<String> airports = airportLocatingService.localNearestAirports(coordinate);

        airports.forEach(System.out::println);
    }


    @Test
    public void testLocatingAirportAsyn() {

        Mono<SitaAirportResponse> sitaAirportResponseFlux =  airportLocatingService.localNearestAirportsAsyn(coordinate);
        sitaAirportResponseFlux
                                .map(sitaAirportResponse -> sitaAirportResponse.getAirports())
                                .doOnNext(sitaAirportResponse -> System.out.println(sitaAirportResponse))
                                .block();

    }
}
