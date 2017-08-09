package com.datalex.eventia.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HotelPriceInfo implements Comparable<HotelPriceInfo> {

    private String id;

    @JsonProperty("agent_prices")
    private List<AgentPriceInfo> agentPrices;


    @Override
    public int compareTo(HotelPriceInfo o) {
        if (o != null
                && o.getAgentPrices() != null
                && o.getAgentPrices().get(0) != null
                && this.getAgentPrices() != null
                && this.getAgentPrices().get(0) != null)
            return this.getAgentPrices().get(0).compareTo(o.getAgentPrices().get(0));
        else
            return 1;
    }
}
