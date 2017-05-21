package com.datalex.eventia.service;

import com.datalex.eventia.ApplicationProperties;
import com.datalex.eventia.domain.*;
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
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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


    public List<Hotel> findHotels(Event event) {
        if (event == null) throw new IllegalArgumentException("Event not found");
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

        List<Hotel> hotels = new ArrayList<>();
        List<HotelInfo> hotelInfos;
        if (event.getCategory().equals("conferences")) {
            int nightStay = (int) DAYS.between(eventStartLocalDate.minusDays(1), eventEndLocalDate.plusDays(1));
            hotelInfos = findExpensiveHotelInfo(pullSessionResponse);
            Collections.reverse(hotelPriceInfos);

            for (HotelInfo hotelInfo : hotelInfos) {
                Hotel hotel = new Hotel();
                hotel.setPrice(hotelPriceInfos.stream()
                        .filter(hotelPriceInfo -> hotelPriceInfo.getId().equals(hotelInfo.getHotel_id()))
                        .map(HotelPriceInfo::getAgentPrices)
                        .flatMap(Collection::stream)
                        .map(AgentPriceInfo::getPriceTotal)
                        .findFirst()
                        .orElse(new BigDecimal("1098")));
                hotel.setName(hotelInfo.getName());
                hotel.setStars(hotelInfo.getStar_rating());
                hotel.setPopularity(hotelInfo.getPopularity());
                hotel.setPopularityDesc(hotelInfo.getPopularity_desc());
                hotel.setNights(nightStay);
                hotels.add(hotel);

            }
        } else {
            int nightStay = (int) DAYS.between(eventStartLocalDate.minusDays(1), eventEndLocalDate.plusDays(2));
            Collections.sort(hotelPriceInfos);
            hotelInfos = findCheapHotelInfo(pullSessionResponse);
            for (HotelInfo hotelInfo : hotelInfos) {
                Hotel hotel = new Hotel();
                hotel.setPrice(hotelPriceInfos.stream()
                        .filter(hotelPriceInfo -> hotelPriceInfo.getId().equals(hotelInfo.getHotel_id()))
                        .map(HotelPriceInfo::getAgentPrices)
                        .flatMap(Collection::stream)
                        .map(AgentPriceInfo::getPriceTotal)
                        .findFirst()
                        .orElse(new BigDecimal("148")));
                hotel.setName(hotelInfo.getName());
                hotel.setStars(hotelInfo.getStar_rating());
                hotel.setPopularity(hotelInfo.getPopularity());
                hotel.setPopularityDesc(hotelInfo.getPopularity_desc());
                hotel.setNights(nightStay);
                hotels.add(hotel);
            }
        }


        return hotels;
    }

    private List<HotelInfo> findExpensiveHotelInfo(PullSessionResponse pullSessionResponse) {
        List<HotelPriceInfo> hotelPriceInfos = pullSessionResponse.getHotelsPrices();
        Collections.reverse(hotelPriceInfos);
        List<String> hotelIds = hotelPriceInfos.stream()
                .map(HotelPriceInfo::getId)
                .limit(3)
                .collect(Collectors.toList());

        String hotelId = hotelPriceInfos.get(hotelPriceInfos.size() - 1).getId();

        List<HotelInfo> hotelInfos = pullSessionResponse.getHotels()
                .stream()
                .filter(hotelInfo1 -> hotelIds.contains(hotelInfo1.getHotel_id()))
                .collect(Collectors.toList());

        return hotelInfos;
    }

    private List<HotelInfo> findCheapHotelInfo(PullSessionResponse pullSessionResponse) {
        List<HotelPriceInfo> hotelPriceInfos = pullSessionResponse.getHotelsPrices();
        List<String> hotelIds = hotelPriceInfos.stream()
                .map(HotelPriceInfo::getId)
                .limit(3)
                .collect(Collectors.toList());

        String hotelId = hotelPriceInfos.get(hotelPriceInfos.size() - 1).getId();

        List<HotelInfo> hotelInfos = pullSessionResponse.getHotels()
                .stream()
                .filter(hotelInfo1 -> hotelIds.contains(hotelInfo1.getHotel_id()))
                .collect(Collectors.toList());

        return hotelInfos;
    }


    private PullSessionResponse pullSessionDetails(String pullSessionRequest) {

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
            } catch (InterruptedException ex) {
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
