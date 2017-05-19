package com.datalex.eventia.service;

import com.datalex.eventia.domain.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by shaojie.xu on 19/05/2017.
 */
@Service
public class AirportLocatingService {

        @Autowired
        RestTemplate restTemplate;


        public String localNearestAirport(Coordinate coordinate){
        
                return null;
        }


}
