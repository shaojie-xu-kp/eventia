package com.datalex.eventia.service;

import com.datalex.eventia.dto.predictHQ.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PredictHQEventServiceTest {

    @Autowired
    PredictHQEventService predictHQEventService;

    @Test
    public void getEvents() throws Exception {
        List<Event> events = predictHQEventService.getEvents("DUB");
        System.out.println(events.size());
        System.out.println(events.get(0));
    }

}