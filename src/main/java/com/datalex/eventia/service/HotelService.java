package com.datalex.eventia.service;

import com.datalex.eventia.ApplicationProperties;
import com.datalex.eventia.domain.Hotel;
import com.datalex.eventia.domain.PullSessionResponse;
import com.datalex.eventia.dto.predictHQ.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@Service
public class HotelService {


    @Autowired
    RestTemplate restTemplate;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private HttpHeaders headers;

    private static final String QUERY = "?";

    @Autowired
    ApplicationProperties applicationProperties;


    @PostConstruct
    private void init() {
        headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    }


    public List<Hotel> findHotels(Event event){
        ZoneId zondIdDestinaion = ZoneId.of(event.getTimezone());
        LocalDate eventStartLocalDate = event.getStart().toInstant().atZone(zondIdDestinaion).toLocalDate();
        LocalDate eventEndLocalDate = event.getEnd().toInstant().atZone(zondIdDestinaion).toLocalDate();
        String startDate = eventStartLocalDate.minusDays(1).format(formatter);
        String endDate = eventEndLocalDate.plusDays(1).format(formatter);

        String createSessionrequest = UriComponentsBuilder
                .fromHttpUrl(applicationProperties.getSkyscannerUrlBase()
                        + applicationProperties.getSkyscannerUrlCreateSession()
                            .replace(applicationProperties.getSkyscannerStartDatePlaceHolder(), startDate)
                            .replace(applicationProperties.getSkyscannerEndDatePlaceHolder(), endDate))
                .build()
                .encode()
                .toUriString();

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        HttpEntity<String> createSessionResponseEntity = restTemplate.exchange(createSessionrequest, HttpMethod.GET, entity, String.class);
        HttpHeaders responseEntityHeaders = createSessionResponseEntity.getHeaders();
        URI location = responseEntityHeaders.getLocation();

        String pullSessionRequest = UriComponentsBuilder
                .fromHttpUrl(applicationProperties.getSkyscannerUrlBase()
                        + location.getPath()
                        + QUERY
                        + location.getQuery())
                .build()
                .encode()
                .toUriString();
        System.out.println(pullSessionRequest);

        HttpEntity<String> pullSessionResponseEntity = restTemplate.exchange(pullSessionRequest, HttpMethod.GET, entity, String.class);

//        ResponseEntity<PullSessionResponse> pullSessionResponseEntity = restTemplate.exchange(pullSessionRequest, HttpMethod.GET, entity, PullSessionResponse.class);

//        pullSessionResponseEntity.getBody().getHotelsPrices()
//                                            .stream()
//                                            .forEach(hotelPriceInfo -> System.out.println(hotelPriceInfo.getId()));

        return null;
    }



}
