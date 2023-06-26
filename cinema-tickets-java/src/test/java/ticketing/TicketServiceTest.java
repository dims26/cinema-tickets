package ticketing;

import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Locale;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public abstract class TicketServiceTest<T extends TicketService> {

    T sut;
    abstract T getSut();

    @Before
    public void setup() {
        this.sut = getSut();
    }

    @Test
    public void givenZeroAcctID_whenPurchaseTickets_thenRaiseException() {
        var id = 0L;

        var exception = assertThrows(InvalidPurchaseException.class,
                () -> sut.purchaseTickets(id));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT).contains("invalid id: 0"));
    }

    @Test
    public void givenNullAcctID_whenPurchaseTickets_thenRaiseException() {
        Long id = null;

        var exception = assertThrows(InvalidPurchaseException.class,
                () -> sut.purchaseTickets(id));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT).contains("invalid id: null"));
    }

    @Test
    public void givenAboveMaxTickets_whenPurchaseTickets_thenRaiseException() {
        var id = 2L;
        var childTickets = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10);
        var infantTickets = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 10);
        var adultTickets = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10);

        var exception = assertThrows(InvalidPurchaseException.class,
                () -> sut.purchaseTickets(id, childTickets, infantTickets, adultTickets));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT)
                .contains("invalid purchase request: above ticket limit"));
    }

    @Test
    public void givenNoAdult_whenPurchaseTickets_thenRaiseException() {
        var id = 2L;
        var childTickets = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 4);
        var infantTickets = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        var adultTickets = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);

        var exception = assertThrows(InvalidPurchaseException.class,
                () -> sut.purchaseTickets(id, childTickets, infantTickets, adultTickets));
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT)
                .contains("invalid purchase request: no adult present"));
    }
}
