package ticketing;

import org.junit.Test;
import uk.gov.dwp.uc.pairtest.ticketing.TicketUtils;
import uk.gov.dwp.uc.pairtest.ticketing.domain.TicketTypeRequest.Type;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.*;
import static uk.gov.dwp.uc.pairtest.ticketing.domain.TicketTypeRequest.Type.*;

public class TicketUtilsTest {

    @Test
    public void givenNullQuantity_whenCalcPrice_thenThrows() {
        var typeToQuantityMap = new HashMap<Type, Integer>();
        typeToQuantityMap.put(ADULT, 2);
        typeToQuantityMap.put(INFANT, null);
        typeToQuantityMap.put(CHILD, 15);
        var typeToPriceMap = new HashMap<Type, Integer>();
        typeToPriceMap.put(ADULT, 20);
        typeToPriceMap.put(CHILD, 10);
        typeToPriceMap.put(INFANT, 0);

        var exception = assertThrows(IllegalStateException.class,
                () -> TicketUtils.calculateTotalPrice(typeToQuantityMap, typeToPriceMap));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT)
                .contains("must not be null"));
    }

    @Test
    public void givenQueriedNullPrice_whenCalcPrice_thenThrows() {
        var typeToQuantityMap = new HashMap<Type, Integer>();
        typeToQuantityMap.put(ADULT, 2);
        typeToQuantityMap.put(INFANT, 3);
        typeToQuantityMap.put(CHILD, 15);
        var typeToPriceMap = new HashMap<Type, Integer>();
        typeToPriceMap.put(ADULT, 20);
        typeToPriceMap.put(CHILD, 10);
        typeToPriceMap.put(INFANT, null);

        var exception = assertThrows(IllegalStateException.class,
                () -> TicketUtils.calculateTotalPrice(typeToQuantityMap, typeToPriceMap));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT)
                .contains("must not be null"));
    }

    @Test
    public void givenMissingPrice_whenCalcPrice_thenThrows() {
        var typeToQuantityMap = new HashMap<Type, Integer>();
        typeToQuantityMap.put(ADULT, 2);
        typeToQuantityMap.put(INFANT, 3);
        typeToQuantityMap.put(CHILD, 15);
        var typeToPriceMap = new HashMap<Type, Integer>();
        typeToPriceMap.put(ADULT, 20);
        typeToPriceMap.put(CHILD, 10);

        var exception = assertThrows(IllegalStateException.class,
                () -> TicketUtils.calculateTotalPrice(typeToQuantityMap, typeToPriceMap));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT)
                .contains("must be a subset"));
    }

    @Test
    public void whenCalcPrice_thenComputesCorrectValue() {
        var typeToQuantityMap = new HashMap<Type, Integer>();
        typeToQuantityMap.put(ADULT, 2);
        typeToQuantityMap.put(INFANT, 3);
        typeToQuantityMap.put(CHILD, 15);
        var typeToPriceMap = new HashMap<Type, Integer>();
        typeToPriceMap.put(ADULT, 20);
        typeToPriceMap.put(CHILD, 10);
        typeToPriceMap.put(INFANT, 0);
        var expected = (typeToQuantityMap.get(ADULT) * typeToPriceMap.get(ADULT))
                + (typeToQuantityMap.get(CHILD) * typeToPriceMap.get(CHILD))
                + (typeToQuantityMap.get(INFANT) * typeToPriceMap.get(INFANT));

        var actual = TicketUtils.calculateTotalPrice(typeToQuantityMap, typeToPriceMap);

        assertEquals(expected, actual);
    }

    @Test
    public void givenNullQuantity_whenCalcNumSeats_thenThrows() {
        var typeToQuantityMap = new HashMap<Type, Integer>();
        typeToQuantityMap.put(ADULT, 2);
        typeToQuantityMap.put(INFANT, null);
        typeToQuantityMap.put(CHILD, 15);
        var eligibleSeating = Set.of(ADULT, CHILD);

        var exception = assertThrows(IllegalStateException.class,
                () -> TicketUtils.calculateRequiredSeats(typeToQuantityMap, eligibleSeating));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT)
                .contains("must not be null"));
    }

    @Test
    public void whenCalcNumSeats_thenComputesCorrectValue() {
        var typeToQuantityMap = new HashMap<Type, Integer>();
        typeToQuantityMap.put(ADULT, 2);
        typeToQuantityMap.put(INFANT, 3);
        typeToQuantityMap.put(CHILD, 15);
        var eligibleSeating = Set.of(ADULT, CHILD);
        var expected = typeToQuantityMap.get(ADULT)
                + typeToQuantityMap.get(CHILD);

        var actual = TicketUtils.calculateRequiredSeats(typeToQuantityMap, eligibleSeating);

        assertEquals(expected, actual);
    }
}
