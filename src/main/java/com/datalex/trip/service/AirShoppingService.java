package com.datalex.trip.service;

import com.datalex.trip.SOAPServiceClient;
import org.iata.iata.edist.AirShoppingRQ;
import org.iata.iata.edist.AirShoppingRS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AirShoppingService {

    @Autowired
    SOAPServiceClient client;

    public AirShoppingRS findFlights(AirShoppingRQ rq) {
        return client.send(rq, AirShoppingRS.class, "AirShopping");
    }

}
