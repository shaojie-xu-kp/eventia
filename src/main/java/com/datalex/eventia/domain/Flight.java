package com.datalex.eventia.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@Data
public class Flight {

    private int id;
    private int price;
    List<OriginDestinarion> originDestinarionList;
}
