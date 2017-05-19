package com.datalex.eventia;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ndc")
@Getter
@Setter
public class ApplicationProperties {

    private String host;

}
