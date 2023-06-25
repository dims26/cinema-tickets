package service;

import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Locale;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public abstract class TicketServiceTest<T extends TicketService> {

    T impl;
    abstract T getImpl();

    @Before
    public void setup() {
        this.impl = getImpl();
    }

    @Test
    public void givenZeroAcctID_whenPurchaseTickets_thenRaiseException() {
        var id = 0L;

        var exception = assertThrows(InvalidPurchaseException.class,
                () -> impl.purchaseTickets(id));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT).contains("invalid id: 0"));
    }

    @Test
    public void givenNullAcctID_whenPurchaseTickets_thenRaiseException() {
        Long id = null;

        var exception = assertThrows(InvalidPurchaseException.class,
                () -> impl.purchaseTickets(id));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT).contains("invalid id: null"));
    }
}
