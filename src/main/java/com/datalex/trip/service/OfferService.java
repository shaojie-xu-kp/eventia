package com.datalex.trip.service;

import com.datalex.trip.ApplicationProperties;
import com.datalex.trip.converter.AirShoppingRSOfferConverterService;
import com.datalex.trip.domain.*;
import com.datalex.trip.domain.Event;
import org.iata.iata.edist.AirShopReqAttributeQueryType;
import org.iata.iata.edist.AirShoppingRQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.StringJoiner;


@Service
public class OfferService {

    @Autowired
    AirShoppingService airShoppingService;

    @Autowired
    AirShoppingRSOfferConverterService airShopingRSOfferConverterService;

    @Autowired
    AirportLocatingService airportLocatingService;

    @Autowired
    private Jaxb2Marshaller unmarshaller;

    @Autowired
    private ApplicationProperties properties;

    @Autowired
    private HotelService hotelService;

    @Autowired
    IndigoService indigoService;


    @Autowired
    private ResourceLoader resourceLoader;


    Object unmarshalObject(String filePath) throws IOException {
        try (InputStream is = resourceLoader.getResource(buildPath(filePath)).getInputStream()) {
            return unmarshaller.unmarshal(new StreamSource(is));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static String buildPath(String fileName) {
        StringJoiner joiner = new StringJoiner(":");
        return joiner.add("classpath").add(fileName).toString();
    }



    public AirShoppingRQ getAirShoppingRQ(String origin, Event e, String airport) {
        ZoneId zonedIdDestination = ZoneId.of(e.getTimezone());
        LocalDate eventStartLocalDate = e.getStart().toInstant().atZone(zonedIdDestination).toLocalDate();
        LocalDate eventEndLocalDate = e.getEnd().toInstant().atZone(zonedIdDestination).toLocalDate();

        AirShoppingRQ rq = null;
        try {
            rq = (AirShoppingRQ) unmarshalObject("request.xml");
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        AirShopReqAttributeQueryType.OriginDestination outbound = rq.getCoreQuery().getOriginDestinations().getOriginDestination().get(0);
        outbound.getDeparture().getAirportCode().setValue(origin);
        outbound.getArrival().getAirportCode().setValue(airport);
        outbound.getDeparture().setDate(eventStartLocalDate.minusDays(1));

        AirShopReqAttributeQueryType.OriginDestination inbound = rq.getCoreQuery().getOriginDestinations().getOriginDestination().get(1);
        inbound.getDeparture().getAirportCode().setValue(airport);
        inbound.getArrival().getAirportCode().setValue(origin);
        inbound.getDeparture().setDate(eventEndLocalDate.plusDays(1));

        return rq;
    }

    private String findClosestJetBlueAirport(Event e) {
        String lon = e.getLocation().get(0);
        String lat = e.getLocation().get(1);
        Coordinate coordinate = new Coordinate(lon, lat);
        List<String> airports = airportLocatingService.localNearestAirports(coordinate);

        return airports.stream()
                .filter(ap -> properties.getPreLoadedCities().contains(ap))
                .findFirst()
                .orElse("");
    }




}
