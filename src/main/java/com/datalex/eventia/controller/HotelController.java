package com.datalex.eventia.controller;

import com.datalex.eventia.domain.Hotel;
import com.datalex.eventia.dto.predictHQ.Event;
import com.datalex.eventia.service.EventService;
import com.datalex.eventia.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by shaojie.xu on 21/05/2017.
 */
@RestController
public class HotelController {

    @Autowired
    HotelService hotelService;

    @Autowired
    EventService eventService;

    @RequestMapping(value = "/hotels/{event_id}", method = RequestMethod.GET)
    @ResponseStatus( HttpStatus.OK )
    public List<Hotel> getBestHotelByEventId(@PathVariable( "event_id" ) String eventId){

        Event event = eventService.getEventById(eventId);
        return hotelService.findHotels(event);

    }
}
