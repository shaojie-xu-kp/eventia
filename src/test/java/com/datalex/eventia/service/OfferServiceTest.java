package com.datalex.eventia.service;

import com.datalex.eventia.domain.Offer;
import org.iata.iata.edist.AirShoppingRQ;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OfferServiceTest {

    @Autowired
    OfferService offerService;

    @Autowired
    EventService eventService;


    @Test
    public void testOffer(){
        String eventId = eventService.getPreLoadedEvents().get(4).getId();
        Offer rs = offerService.getBestOffer("BOS", eventId);
        System.out.println(rs);
        // assert something
    }

}
