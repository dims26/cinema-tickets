package uk.gov.dwp.uc.pairtest.ticketing;

import uk.gov.dwp.uc.pairtest.ticketing.domain.TicketTypeRequest;

import java.util.Map;
import java.util.Set;

public class TicketUtils {

    /** Calculate total price given categorised ticket quantities and prices
     * @param typeQuantityMap Mapping between ticket type and quantity
     * @param typePriceMap Mapping between ticket type and price
     * @return Summed price of all tickets
     * @throws IllegalArgumentException if either map contains null values or typePriceMap has incomplete values
     */
    public static int calculateTotalPrice(Map<TicketTypeRequest.Type, Integer> typeQuantityMap,
                                          Map<TicketTypeRequest.Type, Integer> typePriceMap) throws IllegalArgumentException {
        var result = 0;
        for (TicketTypeRequest.Type type : typeQuantityMap.keySet()) {
            if (!typePriceMap.containsKey(type))
                throw new IllegalArgumentException("Type in typeQuantityMap must be a subset of types in typePriceMap");
            if (typeQuantityMap.get(type) == null || typePriceMap.get(type) == null)
                throw new IllegalArgumentException("Queried value must not be null");

            result += typeQuantityMap.get(type) * typePriceMap.get(type);
        }
        return result;
    }

    /** Calculate total required seats given categorised ticket quantities and allowable seating
     * @param typeQuantityMap Mapping between ticket type and quantity
     * @param seating Set of ticket types requiring seating
     * @return Summed number of required seats
     * @throws IllegalArgumentException if typeQuantityMap contains null values
     */
    public static int calculateRequiredSeats(Map<TicketTypeRequest.Type, Integer> typeQuantityMap,
                                             Set<TicketTypeRequest.Type> seating) throws IllegalArgumentException {
        var result = 0;
        for (TicketTypeRequest.Type type : typeQuantityMap.keySet()) {
            if (typeQuantityMap.get(type) == null)
                throw new IllegalArgumentException("Queried value must not be null");

            if (seating.contains(type)) result += typeQuantityMap.get(type);
        }
        return result;
    }
}
