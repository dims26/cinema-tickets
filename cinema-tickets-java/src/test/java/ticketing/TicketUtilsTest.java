package ticketing;

import org.junit.Test;
import uk.gov.dwp.uc.pairtest.ticketing.TicketUtils;
import uk.gov.dwp.uc.pairtest.ticketing.domain.TicketTypeRequest.Type;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import static org.junit.Assert.*;
import static uk.gov.dwp.uc.pairtest.ticketing.domain.TicketTypeRequest.Type.*;

public class TicketUtilsTest {

    @Test
    public void givenNullQuantity_whenCalcPrice_thenThrows() {
        var typeQuantityMap = new HashMap<Type, Integer>();
        typeQuantityMap.put(ADULT, 2);
        typeQuantityMap.put(INFANT, null);
        typeQuantityMap.put(CHILD, 15);
        var typePriceMap = new HashMap<Type, Integer>();
        typePriceMap.put(ADULT, 20);
        typePriceMap.put(CHILD, 10);
        typePriceMap.put(INFANT, 0);

        var exception = assertThrows(IllegalStateException.class,
                () -> TicketUtils.calcTotalPrice(typeQuantityMap, typePriceMap));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT)
                .contains("must not be null"));
    }

    @Test
    public void givenQueriedNullPrice_whenCalcPrice_thenThrows() {
        var typeQuantityMap = new HashMap<Type, Integer>();
        typeQuantityMap.put(ADULT, 2);
        typeQuantityMap.put(INFANT, 3);
        typeQuantityMap.put(CHILD, 15);
        var typePriceMap = new HashMap<Type, Integer>();
        typePriceMap.put(ADULT, 20);
        typePriceMap.put(CHILD, 10);
        typePriceMap.put(INFANT, null);

        var exception = assertThrows(IllegalStateException.class,
                () -> TicketUtils.calcTotalPrice(typeQuantityMap, typePriceMap));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT)
                .contains("must not be null"));
    }

    @Test
    public void givenMissingPrice_whenCalcPrice_thenThrows() {
        var typeQuantityMap = new HashMap<Type, Integer>();
        typeQuantityMap.put(ADULT, 2);
        typeQuantityMap.put(INFANT, 3);
        typeQuantityMap.put(CHILD, 15);
        var typePriceMap = new HashMap<Type, Integer>();
        typePriceMap.put(ADULT, 20);
        typePriceMap.put(CHILD, 10);

        var exception = assertThrows(IllegalStateException.class,
                () -> TicketUtils.calcTotalPrice(typeQuantityMap, typePriceMap));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT)
                .contains("must be a subset"));
    }

    @Test
    public void whenCalcPrice_thenComputesCorrectValue() {
        var typeQuantityMap = new HashMap<Type, Integer>();
        typeQuantityMap.put(ADULT, 2);
        typeQuantityMap.put(INFANT, 3);
        typeQuantityMap.put(CHILD, 15);
        var typePriceMap = new HashMap<Type, Integer>();
        typePriceMap.put(ADULT, 20);
        typePriceMap.put(CHILD, 10);
        typePriceMap.put(INFANT, 0);
        var expected = (typeQuantityMap.get(ADULT) * typePriceMap.get(ADULT))
                + (typeQuantityMap.get(CHILD) * typePriceMap.get(CHILD))
                + (typeQuantityMap.get(INFANT) * typePriceMap.get(INFANT));

        var actual = TicketUtils.calcTotalPrice(typeQuantityMap, typePriceMap);

        assertEquals(expected, actual);
    }

    @Test
    public void givenNullQuantity_whenCalcNumSeats_thenThrows() {
        var typeQuantityMap = new HashMap<Type, Integer>();
        typeQuantityMap.put(ADULT, 2);
        typeQuantityMap.put(INFANT, null);
        typeQuantityMap.put(CHILD, 15);
        var eligibleSeating = Arrays.asList(ADULT, CHILD);

        var exception = assertThrows(IllegalStateException.class,
                () -> TicketUtils.calcNumSeats(typeQuantityMap, eligibleSeating));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT)
                .contains("must not be null"));
    }

    @Test
    public void whenCalcNumSeats_thenComputesCorrectValue() {
        var typeQuantityMap = new HashMap<Type, Integer>();
        typeQuantityMap.put(ADULT, 2);
        typeQuantityMap.put(INFANT, 3);
        typeQuantityMap.put(CHILD, 15);
        var eligibleSeating = Arrays.asList(ADULT, CHILD);
        var expected = typeQuantityMap.get(ADULT)
                + typeQuantityMap.get(CHILD);

        var actual = TicketUtils.calcNumSeats(typeQuantityMap, eligibleSeating);

        assertEquals(expected, actual);
    }
}
