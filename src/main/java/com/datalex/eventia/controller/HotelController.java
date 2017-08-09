package com.datalex.eventia.controller;

import com.datalex.eventia.domain.Hotel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by shaojie.xu on 21/05/2017.
 */
@RestController
public class HotelController {


    @RequestMapping(value = "/hotels/{event_id}", method = RequestMethod.GET)
    @ResponseStatus( HttpStatus.OK )
    public List<Hotel> getBestHotelByEventId(@PathVariable( "event_id" ) String eventId){

        return null;

    }
}
