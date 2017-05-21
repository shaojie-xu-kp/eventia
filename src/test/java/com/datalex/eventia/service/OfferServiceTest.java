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

//    @Test
//    public void testAirShoppingRQ()
//    {
//        AirShoppingRQ rq =  offerService.getAirShoppingRQ("BOS", "LxG6KlaYNNVA");
//        // assert something
//    }
//
//
//    @Test
//    public void testOffer(){
//        Offer rs = offerService.getBestOffer("BOS", "LxG6KlaYNNVA");
//        // assert something
//    }

}
