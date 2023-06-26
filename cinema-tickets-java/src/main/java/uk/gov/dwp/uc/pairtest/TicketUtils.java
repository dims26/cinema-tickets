package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.Map;

public class TicketUtils {

    public static int calcTotalPrice(Map<TicketTypeRequest.Type, Integer> typeQuantityMap,
                                     Map<TicketTypeRequest.Type, Integer> typePriceMap) {
        var result = 0;
        for (TicketTypeRequest.Type type : typeQuantityMap.keySet()) {
            if (!typePriceMap.containsKey(type))
                throw new IllegalStateException("Type in typeQuantityMap must be a subset of types in typePriceMap");
            if (typeQuantityMap.get(type) == null || typePriceMap.get(type) == null)
                throw new IllegalStateException("Queried value must not be null");

            result += typeQuantityMap.get(type) * typePriceMap.get(type);
        }
        return result;
    }
}
