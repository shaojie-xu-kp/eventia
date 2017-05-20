package com.datalex.eventia.service;

import com.datalex.eventia.ApplicationProperties;
import com.datalex.eventia.converter.ConvertService;
import com.datalex.eventia.domain.Coordinate;
import com.datalex.eventia.domain.Offer;
import com.datalex.eventia.dto.predictHQ.Event;
import org.iata.iata.edist.AirShopReqAttributeQueryType;
import org.iata.iata.edist.AirShoppingRQ;
import org.iata.iata.edist.AirShoppingRS;
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

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@Service
public class OfferService {

    @Autowired
    AirShoppingService airShoppingService;

    @Autowired
    ConvertService<AirShoppingRS, Offer> airShopingRSOfferConverterService;

    @Autowired
    AirportLocatingService airportLocatingService;

    @Autowired
    private Jaxb2Marshaller unmarshaller;

    @Autowired
    EventService predictHQEventService;

    @Autowired
    private ApplicationProperties properties;


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

    public Offer getBestOffer(String origin, String eventId){

        AirShoppingRQ rq = getAirShoppingRQ(origin, eventId);

        AirShoppingRS flights = airShoppingService.findFlights(rq);

        return airShopingRSOfferConverterService.convert(flights);
    }


    public AirShoppingRQ getAirShoppingRQ(String origin, String eventId) {
        Event e = predictHQEventService.getEventById(eventId);
        ZoneId zondIdDestinaion = ZoneId.of(e.getTimezone());
        LocalDate eventStartLocalDate = e.getStart().toInstant().atZone(zondIdDestinaion).toLocalDate();
        LocalDate eventEndLocalDate = e.getEnd().toInstant().atZone(zondIdDestinaion).toLocalDate();

        String lon = e.getLocation().get(0);
        String lat = e.getLocation().get(1);
        Coordinate coordinate = new Coordinate(lon, lat);
        List<String> airports = airportLocatingService.localNearestAirports(coordinate);

        String airport = airports.stream()
                                    .filter(ap -> properties.getPreLoadedCities().contains(ap))
                                    .findFirst()
                                    .orElse("");

        AirShoppingRQ rq = null;
        try {
            rq = (AirShoppingRQ) unmarshalObject("AirShoppingRQ.xml");
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


    public static void main(String... args){

        ZoneId id1 = ZoneId.of("America/New_York");
        System.out.println(id1);
    }



}
