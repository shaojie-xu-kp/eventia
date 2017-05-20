package com.datalex.eventia;

import com.datalex.eventia.domain.Coordinate;
import com.datalex.eventia.dto.predictHQ.Event;
import com.datalex.eventia.service.AirShoppingService;
import com.datalex.eventia.service.AirportLocatingService;
import com.datalex.eventia.service.PredictHQEventService;
import org.apache.commons.lang3.StringUtils;
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
        String origin = "JFK";
        List<Event> events = predictHQEventService.getEvents("BOS");
        Event e = events.stream()
                .filter(event -> event.getTitle().equals(eventName))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        String lon = e.getLocation().get(0);
        String lat = e.getLocation().get(1);
        Coordinate coordinate = new Coordinate(lon, lat);
        String airport = airportLocatingService.localNearestAirport(coordinate);
        AirShoppingRQ rq = (AirShoppingRQ) unmarshalObject("AirShoppingRQ.xml");
        rq.getCoreQuery().getOriginDestinations().getOriginDestination()
                .get(0).getDeparture().getAirportCode().setValue(origin);
        rq.getCoreQuery().getOriginDestinations().getOriginDestination()
                .get(0).getArrival().getAirportCode().setValue(airport);
        System.out.println(airport);
    }
}
