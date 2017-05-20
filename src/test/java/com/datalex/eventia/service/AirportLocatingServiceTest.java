package com.datalex.eventia.service;

import com.datalex.eventia.domain.Coordinate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by shaojie.xu on 19/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AirportLocatingServiceTest {

    @Autowired
    AirportLocatingService airportLocatingService;

    private static final String IATA_AMS = "AMS";

    private static final String IATA_JFK = "JFK";

    @Test
    public void testAirportAmsterdam(){
        List<String> airports = airportLocatingService.localNearestAirports(new Coordinate("4.879903", "52.297097"));
        assertThat(airports).contains(IATA_AMS);
    }

    @Test
    public void testAirportNewyork(){
        List<String> airports = airportLocatingService.localNearestAirports(new Coordinate("-73.850201", "40.679196"));
        assertThat(airports).contains(IATA_JFK);
    }


}
