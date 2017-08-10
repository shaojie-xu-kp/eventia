package com.datalex.trip.converter;

import com.datalex.trip.domain.*;
import com.datalex.trip.domain.Departure;
import com.datalex.trip.domain.Flight;
import com.datalex.trip.domain.Offer;
import org.iata.iata.edist.*;
import org.iata.iata.edist.AirShoppingRS.OffersGroup.AirlineOffers.AirlineOffer;
import org.iata.iata.edist.OriginDestination;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class AirShoppingRSOfferConverterService {

    public Offer convert(AirShoppingRS airShoppingRS, String origin, String destination) {
        List<Flight> flights = airShoppingRS.getOffersGroup().getAirlineOffers().stream()
                .flatMap(airlineOffers -> airlineOffers.getAirlineOffer().stream())
                .map(this::airlineOffer2Flight)
                .collect(Collectors.toList());

        List<Flight> outFlights = flights.stream().filter(f ->
                f.getOriginDestinations().get(0).getDeparture().getAirportCode().equals(origin) &&
                        f.getOriginDestinations().get(0).getArrival().getAirportCode().equals(destination))
                .limit(10)
                .collect(Collectors.toList());

        List<Flight> inFlights = flights.stream().filter(f ->
                f.getOriginDestinations().get(0).getDeparture().getAirportCode().equals(destination) &&
                        f.getOriginDestinations().get(0).getArrival().getAirportCode().equals(origin))
                .limit(10)
                .collect(Collectors.toList());
        List<Flight> merged = IntStream.range(0, 10)
                .mapToObj(i -> {
            Flight out = outFlights.get(i);
            Flight in = inFlights.get(i);
            return mergeFlights(out, in);
        }).collect(Collectors.toList());

        Offer offer = new Offer();
        offer.setFlights(merged);
        return offer;
    }

    private Flight mergeFlights(Flight outbound, Flight inbound) {
        outbound.getOriginDestinations().addAll(inbound.getOriginDestinations());
        return outbound;
    }

    private Flight airlineOffer2Flight(AirlineOffer airlineOffer) {
        PricedFlightOfferType.OfferPrice airOfferItem = airlineOffer.getPricedOffer().getOfferPrice().get(0);
        BigDecimal price = airOfferItem.getRequestedDate().getPriceDetail().getTotalAmount()
                .getDetailCurrencyPrice().getTotal().getValue();
        String id = airlineOffer.getOfferID().getValue();
        List<com.datalex.trip.domain.OriginDestination> originDestinations =
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


    private com.datalex.trip.domain.OriginDestination convertOriginDestination(OriginDestination originDestination) {
        List<ListOfFlightSegmentType> segments = originDestination.getFlightReferences().getValue().stream()
                .map(o -> (DataListType.FlightList.Flight) o)
                .flatMap(flight -> flight.getSegmentReferences().getValue().stream())
                .map(o -> (ListOfFlightSegmentType) o)
                .collect(Collectors.toList());
        ListOfFlightSegmentType originSegment = segments.get(0);
        ListOfFlightSegmentType destinationSegment = segments.get(segments.size() - 1);

        com.datalex.trip.domain.OriginDestination result = new com.datalex.trip.domain.OriginDestination();
        Departure departure = new Departure();
        departure.setAirportCode(originSegment.getDeparture().getAirportCode().getValue());
        departure.setDate(originSegment.getDeparture().getDate().format(DateTimeFormatter.ISO_DATE));
        departure.setTime(originSegment.getDeparture().getFlightDepartureTime());

        Arrival arrival = new Arrival();
        arrival.setAirportCode(destinationSegment.getArrival().getAirportCode().getValue());
        arrival.setDate(destinationSegment.getArrival().getDate().format(DateTimeFormatter.ISO_DATE));
        arrival.setTime(destinationSegment.getArrival().getFlightArrivalTime());

        result.setDeparture(departure);
        result.setArrival(arrival);

        MarketingCarrierFlightType marketingCarrier = destinationSegment.getMarketingCarrier();

        result.setFlightNumber(marketingCarrier.getAirlineID().getValue() + marketingCarrier.getFlightNumber().getValue());

        return result;
    }
}
