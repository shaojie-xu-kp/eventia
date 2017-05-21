package com.datalex.eventia.service;

import com.datalex.eventia.ApplicationProperties;
import com.datalex.eventia.domain.Hotel;
import com.datalex.eventia.domain.HotelInfo;
import com.datalex.eventia.domain.HotelPriceInfo;
import com.datalex.eventia.domain.PullSessionResponse;
import com.datalex.eventia.dto.predictHQ.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@Service
public class HotelService {


    @Autowired
    RestTemplate restTemplate;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private ObjectMapper objectMapper;

    private HttpHeaders headers;

    private static final String QUERY = "?";

    @Autowired
    ApplicationProperties applicationProperties;

    ObjectMapper mapper = new ObjectMapper();



    @PostConstruct
    private void init() {
        headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    }


    public Hotel findHotels(Event event){
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
        System.out.println(createSessionrequest);

        HttpEntity<String> entity = new HttpEntity<>(headers);
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

        PullSessionResponse pullSessionResponse = pullSessionDetails(pullSessionRequest);
        List<HotelPriceInfo> hotelPriceInfos = pullSessionResponse.getHotelsPrices();
        Collections.sort(hotelPriceInfos);
        pullSessionResponse.setHotelsPrices(hotelPriceInfos);

        Hotel hotel = new Hotel();
        hotel.setNights((int)DAYS.between(eventStartLocalDate.minusDays(1), eventEndLocalDate.plusDays(1)));
        HotelInfo hotelInfo = null;
        if(event.getCategory().equals("conferences"))
        {
            hotelInfo = findExpensiveHotelInfo(pullSessionResponse);
            Collections.reverse(hotelPriceInfos);
            hotel.setPrice(hotelPriceInfos.stream()
                                            .map(hotelPriceInfo1 -> hotelPriceInfo1.getAgentPrices())
                                            .flatMap(agentPriceInfos -> agentPriceInfos.stream())
                                            .map(agentPriceInfo -> agentPriceInfo.getPriceTotal())
                                            .findFirst()
                                            .orElse(879));
        }else{
            Collections.sort(hotelPriceInfos);
            hotelInfo = findCheapHotelInfo(pullSessionResponse);
            hotel.setPrice(hotelPriceInfos.stream()
                            .map(hotelPriceInfo1 -> hotelPriceInfo1.getAgentPrices())
                            .flatMap(agentPriceInfos -> agentPriceInfos.stream())
                            .map(agentPriceInfo -> agentPriceInfo.getPriceTotal())
                            .findFirst()
                            .orElse(286));
        }

        hotel.setName(hotelInfo.getName());
        hotel.setStars(hotelInfo.getStar_rating());
        hotel.setPopularity(hotelInfo.getPopularity());
        hotel.setPopularityDesc(hotelInfo.getPopularity_desc());

        return hotel;
    }

    private HotelInfo findExpensiveHotelInfo(PullSessionResponse pullSessionResponse) {
        List<HotelPriceInfo> hotelPriceInfos = pullSessionResponse.getHotelsPrices();
        String hotelId = hotelPriceInfos.get(hotelPriceInfos.size()-1).getId();
        HotelInfo hotelInfo = pullSessionResponse.getHotels()
                                    .stream()
                                    .filter(hotelInfo1 -> hotelInfo1.getHotel_id().equals(hotelId))
                                    .findFirst()
                                    .orElse(null);
        return hotelInfo;
    }

    private HotelInfo findCheapHotelInfo(PullSessionResponse pullSessionResponse) {
        List<HotelPriceInfo> hotelPriceInfos = pullSessionResponse.getHotelsPrices();
        String hotelId = hotelPriceInfos.get(0).getId();
        HotelInfo hotelInfo = pullSessionResponse.getHotels()
                .stream()
                .filter(hotelInfo1 -> hotelInfo1.getHotel_id().equals(hotelId))
                .findFirst()
                .orElse(null);
        return hotelInfo;
    }



    private PullSessionResponse pullSessionDetails(String pullSessionRequest){

        PullSessionResponse pullSessionResponse = null;
        try {

            URL url = new URL(pullSessionRequest);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            try {
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            StringBuilder builder = new StringBuilder();
            String response = "";

            while ((response = br.readLine()) != null) {
                builder.append(response);
            }

            String text = builder.toString();
            System.out.println(text);
            pullSessionResponse = objectMapper.readValue(text, PullSessionResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pullSessionResponse;

    }






}
