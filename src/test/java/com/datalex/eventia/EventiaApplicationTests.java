package com.datalex.eventia;

import org.apache.commons.lang3.StringUtils;
import org.iata.iata.edist.AirShoppingRQ;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringJoiner;

import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventiaApplicationTests {

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
    public void contextLoads() {
    }

    @Test
    public void testAirShopping() throws Exception {
        AirShoppingRQ rq = (AirShoppingRQ) unmarshalObject("AirShoppingRQ.xml");
    }
}
