package com.datalex.eventia.service;

import com.datalex.eventia.domain.Hotel;
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
public class HotelServiceTest {

    @Autowired
    HotelService hotelService;

    @Autowired
    EventService predictHQEventService;

    @Test
    public void testIndividuleId(){
        Hotel hotel = hotelService.findHotels(predictHQEventService.getEvents("JFK").get(4));
        System.out.println(hotel);
    }

}
