package com.datalex.eventia;

import com.datalex.eventia.domain.Coordinate;
import com.datalex.eventia.dto.predictHQ.Event;
import com.datalex.eventia.service.AirShoppingService;
import com.datalex.eventia.service.AirportLocatingService;
import com.datalex.eventia.service.PredictHQEventService;
import org.apache.commons.lang3.StringUtils;
import org.iata.iata.edist.AirShopReqAttributeQueryType;
import org.iata.iata.edist.AirShopReqAttributeQueryType.OriginDestination;
import org.iata.iata.edist.AirShoppingRQ;
import org.iata.iata.edist.AirShoppingRS;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.StringJoiner;

import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventiaApplicationTests {

    @Autowired
    private Jaxb2Marshaller unmarshaller;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private AirShoppingService airShoppingService;
    @Autowired
    private PredictHQEventService predictHQEventService;
    @Autowired
    private AirportLocatingService airportLocatingService;

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


    @Test
    public void contextLoads() {
    }

    @Test
    public void testAirShopping() throws Exception {
        AirShoppingRQ rq = (AirShoppingRQ) unmarshalObject("AirShoppingRQ.xml");
        AirShoppingRS flights = airShoppingService.findFlights(rq);
        assertThat(flights).isNotNull();
        unmarshaller.marshal(flights, new StreamResult(System.out));
        int size = flights.getOffersGroup().getAirlineOffers().size();
        System.out.println(size);
    }

    @Test
    public void endToEnd() throws Exception {
        String eventName = "Dirty Heads with SOJA";
        LocalDate now = LocalDate.now();
        String origin = "JFK";
        List<Event> events = predictHQEventService.getEvents("BOS");
        Event e = events.stream()
                .filter(event -> event.getTitle().equals(eventName))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        String lon = e.getLocation().get(0);
        String lat = e.getLocation().get(1);
        Coordinate coordinate = new Coordinate(lon, lat);
        List<String> airports = airportLocatingService.localNearestAirports(coordinate);
        AirShoppingRQ rq = (AirShoppingRQ) unmarshalObject("request.xml");
        OriginDestination outbound = rq.getCoreQuery().getOriginDestinations().getOriginDestination().get(0);
        outbound.getDeparture().getAirportCode().setValue(origin);
        String airport = airports.get(0);
        outbound.getArrival().getAirportCode().setValue(airport);
        outbound.getDeparture().setDate(now.plusMonths(2));

        OriginDestination inbound = rq.getCoreQuery().getOriginDestinations().getOriginDestination().get(1);
        inbound.getDeparture().getAirportCode().setValue(airport);
        inbound.getArrival().getAirportCode().setValue(origin);
        inbound.getDeparture().setDate(now.plusMonths(2).plusDays(4));

        AirShoppingRS flights = airShoppingService.findFlights(rq);

        unmarshaller.marshal(flights, new StreamResult(System.out));

        // TODO: adjust dates, filter airports, hotels (skyscanner), ground transport (indigo)
        // TODO: filter options so that flight arrives before the event starts
    }
}
