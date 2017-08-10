package com.datalex.trip.controller;

import com.datalex.trip.domain.Hotel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;


@RestController
public class HotelController {


    @GetMapping("/hotels/{event_id}")
    @ResponseStatus( HttpStatus.OK )
    public Flux<Hotel> getBestHotelByEventId(@PathVariable( "event_id" ) String eventId){

        return null;

    }
}
