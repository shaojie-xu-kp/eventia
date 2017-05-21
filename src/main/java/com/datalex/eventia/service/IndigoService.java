package com.datalex.eventia.service;

import com.datalex.eventia.domain.Taxi;
import com.datalex.eventia.dto.indigo.IndigoResponse;
import com.datalex.eventia.dto.indigo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndigoService {

    @Autowired
    private RestTemplate restTemplate;

    public List<Taxi> getTaxis(String flightNumber, String arrivalDate, String eventLat, String eventLon) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String query = UriComponentsBuilder.fromHttpUrl("https://orange.indigo-connect.com/e/3/search")
                .queryParam("api_key", "priv_izCVTGjLpXcphokZ")
                .queryParam("from_type", "flight")
                .queryParam("from", flightNumber)
                .queryParam("to_type", "geo")
                .queryParam("to", eventLat + "," + eventLon)
                .queryParam("currency", "USD")
                .queryParam("out_dt", arrivalDate)
                .build()
                .encode()
                .toUriString();

        IndigoResponse response = restTemplate.getForObject(query, IndigoResponse.class);
        return response.getResults().stream().map(this::result2Taxi).collect(Collectors.toList());
    }

    private Taxi result2Taxi(Result result) {
        Taxi taxi = new Taxi();
        taxi.setPrice(result.getCostTotal().substring(3));
        taxi.setOperator(result.getOut().getOperator());
        return taxi;
    }
}
