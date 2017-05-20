package com.datalex.eventia.Converter;

import com.datalex.eventia.domain.Offer;
import org.iata.iata.edist.AirShoppingRS;
import org.springframework.stereotype.Service;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@Service
public class AirShopingRSOfferConverterService implements ConvertService<AirShoppingRS, Offer>{

    @Override
    public Offer convert(AirShoppingRS airShoppingRS) {
        return null;
    }
}
