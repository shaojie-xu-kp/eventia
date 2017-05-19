package com.datalex.eventia;

import org.iata.iata.edist.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class EventiaApplication {

    @Autowired
    ApplicationProperties properties;

    public static void main(String[] args) {
        SpringApplication.run(EventiaApplication.class, args);
    }

    @Bean
    public SOAPServiceClient soapServiceClient() {
        return new SOAPServiceClient();
    }

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        Class<?>[] classesToBound = new Class[]{AirShoppingRQ.class, AirShoppingRS.class};
        marshaller.setClassesToBeBound(classesToBound);
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setDefaultUri(properties.getHost());
        webServiceTemplate.setMarshaller(jaxb2Marshaller());
        webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
        return webServiceTemplate;
    }
}
