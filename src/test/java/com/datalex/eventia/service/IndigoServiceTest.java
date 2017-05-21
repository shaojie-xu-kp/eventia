package com.datalex.eventia.service;

import com.datalex.eventia.domain.Offer;
import com.datalex.eventia.domain.OriginDestination;
import com.datalex.eventia.domain.Taxi;
import com.datalex.eventia.dto.predictHQ.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IndigoServiceTest {

    @Autowired
    PredictHQEventService predictHQEventService;

    @Autowired
    IndigoService indigoService;

    @Autowired
    OfferService offerService;


    @Test
    public void getTaxis() throws Exception {
        Event e = predictHQEventService.getEventById("8K2jWeKbljl9");
        Offer bestOffer = offerService.getBestOffer("LAX", "8K2jWeKbljl9");
        String lon = e.getLocation().get(0);
        String lat = e.getLocation().get(1);
        OriginDestination originDestination = bestOffer.getFlights().get(0).getOriginDestinations().get(0);
        String flightNumber = originDestination.getFlightNumber();
        String arrivalDate = originDestination.getArrival().getDate();
        List<Taxi> taxis = indigoService.getTaxis(flightNumber, arrivalDate, lat, lon);

    }

}