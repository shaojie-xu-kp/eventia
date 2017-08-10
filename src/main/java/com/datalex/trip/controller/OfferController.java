package com.datalex.trip.controller;

import com.datalex.trip.domain.Offer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
public class OfferController {


    @GetMapping("/offer/{event_id}/{origin}")
    @ResponseStatus( HttpStatus.OK )
    public Mono<Offer> findOffer(@PathVariable( "origin" ) String origin, @PathVariable( "event_id" ) String eventId){
        return null;
    }
}
