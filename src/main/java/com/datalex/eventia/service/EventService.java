package com.datalex.eventia.service;

import com.datalex.eventia.dto.predictHQ.Event;

import java.util.List;

public interface EventService {

    List<Event> getEvents(String city);

    List<Event> getPreLoadedEvents();

    Event getEventById(String id);

}
