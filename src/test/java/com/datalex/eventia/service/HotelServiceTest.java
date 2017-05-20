package com.datalex.eventia.service;

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
    EventService eventService;

    @Test
    public void testIndividuleId(){
        hotelService.findHotels(eventService.getEventById("5doK6E4dLdEa"));
    }
}
