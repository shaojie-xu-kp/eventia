package com.datalex.eventia.service;

import com.datalex.eventia.domain.Coordinate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;


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
        String airports = airportLocatingService.localNearestAirport(new Coordinate("4.879903", "52.297097"));
        assertTrue(airports.equals(IATA_AMS));
    }

    @Test
    public void testAirportNewyork(){
        String airports = airportLocatingService.localNearestAirport(new Coordinate("-73.850201", "40.679196"));
        assertTrue(airports.equals(IATA_JFK));
    }


}
