package com.datalex.eventia.converter;

import com.datalex.eventia.domain.*;
import com.datalex.eventia.domain.Departure;
import com.datalex.eventia.domain.Flight;
import com.datalex.eventia.domain.Offer;
import org.iata.iata.edist.*;
import org.iata.iata.edist.AirShoppingRS.OffersGroup.AirlineOffers.AirlineOffer;
import org.iata.iata.edist.OriginDestination;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@Service
public class AirShopingRSOfferConverterService implements ConvertService<AirShoppingRS, Offer>{

    @Override
    public Offer convert(AirShoppingRS airShoppingRS) {
        List<Flight> flights = airShoppingRS.getOffersGroup().getAirlineOffers().stream()
                .flatMap(airlineOffers -> airlineOffers.getAirlineOffer().stream())
                .map(this::airlineOffer2Flight)
                .collect(Collectors.toList());
        Offer offer = new Offer();
        offer.setFlights(flights);
        return offer;
    }

    private Flight airlineOffer2Flight(AirlineOffer airlineOffer) {
        PricedFlightOfferType.OfferPrice airOfferItem = airlineOffer.getPricedOffer().getOfferPrice().get(0);
        BigDecimal price = airOfferItem.getRequestedDate().getPriceDetail().getTotalAmount()
                .getDetailCurrencyPrice().getTotal().getValue();
        String id = airlineOffer.getOfferID().getValue();
        List<com.datalex.eventia.domain.OriginDestination> originDestinations =
                airOfferItem.getRequestedDate().getAssociations().get(0).getApplicableFlight()
                .getOriginDestinationReferences().stream()
                .map(o -> (OriginDestination) o)
                .map(this::convertOriginDestination)
                .collect(Collectors.toList());
        Flight flight = new Flight();
        flight.setId(id);
        flight.setOriginDestinations(originDestinations);
        flight.setPrice(price);
        return flight;
    }

    private com.datalex.eventia.domain.OriginDestination convertOriginDestination(OriginDestination originDestination) {
        List<ListOfFlightSegmentType> segments = originDestination.getFlightReferences().getValue().stream()
                .map(o -> (DataListType.FlightList.Flight) o)
                .flatMap(flight -> flight.getSegmentReferences().getValue().stream())
                .map(o -> (ListOfFlightSegmentType) o)
                .collect(Collectors.toList());
        ListOfFlightSegmentType originSegment = segments.get(0);
        ListOfFlightSegmentType destinationSegment = segments.get(segments.size() - 1);

        com.datalex.eventia.domain.OriginDestination result = new com.datalex.eventia.domain.OriginDestination();
        Departure departure = new Departure();
        departure.setAirportCode(originSegment.getDeparture().getAirportCode().getValue());
        departure.setDate(originSegment.getDeparture().getDate().format(DateTimeFormatter.BASIC_ISO_DATE));
        departure.setTime(originSegment.getDeparture().getFlightDepartureTime());

        Arrival arrival = new Arrival();
        arrival.setAirportCode(destinationSegment.getArrival().getAirportCode().getValue());
        arrival.setDate(destinationSegment.getArrival().getDate().format(DateTimeFormatter.BASIC_ISO_DATE));
        arrival.setTime(destinationSegment.getArrival().getFlightArrivalTime());

        result.setDeparture(departure);
        result.setArrival(arrival);

        return result;
    }
}
