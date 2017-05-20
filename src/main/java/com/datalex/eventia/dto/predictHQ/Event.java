package com.datalex.eventia.dto.predictHQ;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    private String title;
    private String description;
    private String category;
    private Date start;
    private Date end;
    private String timezone;
    private String country;
    private String state;
    private List<String> location;
}
