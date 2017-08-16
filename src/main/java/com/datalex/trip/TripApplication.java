package com.datalex.trip;

import com.datalex.trip.config.ApplicationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.iata.iata.edist.AirShoppingRQ;
import org.iata.iata.edist.AirShoppingRS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ws.client.core.WebServiceTemplate;
import reactor.ipc.netty.http.client.HttpClientOptions;

import java.util.function.Consumer;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class TripApplication {

    @Autowired
    ApplicationProperties properties;


    public static void main(String[] args) {

        SpringApplication.run(TripApplication.class, args);
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
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setDefaultUri(properties.getHost());
        webServiceTemplate.setMarshaller(jaxb2Marshaller());
        webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
        return webServiceTemplate;
    }

    @Bean
    public ObjectMapper jacksonObjectMapper(){
        return new ObjectMapper();
    }

    @Bean
    public WebClient webClient(){
        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(new Consumer<HttpClientOptions.Builder>() {
                    @Override
                    public void accept(HttpClientOptions.Builder builder) {
                        builder.disablePool();
                    }
                }))
                .build();
        return webClient;
    }
}
