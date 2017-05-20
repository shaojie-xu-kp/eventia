package com.datalex.eventia.service;

import com.datalex.eventia.ApplicationProperties;
import com.datalex.eventia.dto.predictHQ.Event;
import com.datalex.eventia.dto.predictHQ.PredictHQResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class PredictHQEventService implements EventService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationProperties properties;

    public List<Event> getEvents(String city) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + properties.getPredictHqAuthorizationKey());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String request = UriComponentsBuilder.fromHttpUrl(properties.getPredictHqUrl())
                .queryParam("place.exact", city)
                .build()
                .encode()
                .toUriString();

        ResponseEntity<PredictHQResponse> exchange =
                restTemplate.exchange(request, HttpMethod.GET, entity, PredictHQResponse.class);
        return exchange.getBody().getResults();
    }


}
