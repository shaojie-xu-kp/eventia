package com.datalex.eventia.converter;

import com.datalex.eventia.domain.Flight;
import com.datalex.eventia.domain.Offer;
import org.iata.iata.edist.AirShoppingRS;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.StringJoiner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AirShoppingRSOfferConverterServiceTest {

    @Autowired
    AirShopingRSOfferConverterService airShopingRSOfferConverterService;

    @Autowired
    private Jaxb2Marshaller unmarshaller;
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

    @Test
    public void test() throws Exception {
        AirShoppingRS rs = (AirShoppingRS) unmarshalObject("AirShoppingRS.xml");
        Offer offer = airShopingRSOfferConverterService.convert(rs);
        List<Flight> flights = offer.getFlights();
        System.out.println(flights.get(0));
    }
}