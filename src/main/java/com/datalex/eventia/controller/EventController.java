package com.datalex.eventia.controller;

import com.datalex.eventia.dto.predictHQ.Event;
import com.datalex.eventia.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@RestController
public class EventController {

    @Autowired
    EventService predictHQEventService;


    @RequestMapping(value = "/events/{city}", method = RequestMethod.GET)
    @ResponseStatus( HttpStatus.OK )
    public List<Event> findEventsByCity(@PathVariable( "city" ) String city){
        return predictHQEventService.getEvents(city.toUpperCase());
    }

    @RequestMapping(value = "/events", method = RequestMethod.GET)
    @ResponseStatus( HttpStatus.OK )
    public List<Event> findAllEvents(){
        return predictHQEventService.getPreLoadedEvents();
    }
}
