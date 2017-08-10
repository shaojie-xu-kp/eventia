package com.datalex.trip.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@Data
public class AgentPriceInfo implements Comparable<AgentPriceInfo>{

    private String  id;

    @JsonProperty("price_total")
    private BigDecimal priceTotal;

    @Override
    public int compareTo(AgentPriceInfo o) {
        if(o != null )
            return priceTotal.compareTo(o.getPriceTotal());
        else
            return 1;
    }
}
