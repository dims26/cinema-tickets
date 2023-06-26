package ticketing;

import org.junit.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.uc.pairtest.TicketServiceImpl.TYPE_PRICE_MAP;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

public class TicketServiceImplTest extends TicketServiceTest<TicketServiceImpl>{

    TicketPaymentService paymentService;
    SeatReservationService reservationService;

    @Override
    TicketServiceImpl getSut() {
        this.paymentService = mock();
        this.reservationService = mock();
        return new TicketServiceImpl(paymentService, reservationService);
    }

    @Test
    public void givenNoInvalidCase_whenPurchaseTickets_thenCalculateCorrectPrice() {
        var id = 2L;
        var childTickets = new TicketTypeRequest(CHILD, 4);
        var infantTickets = new TicketTypeRequest(INFANT, 2);
        var adultTickets = new TicketTypeRequest(ADULT, 1);
        var childPrice = childTickets.getNoOfTickets() *
                TYPE_PRICE_MAP.get(CHILD);
        var infantPrice = infantTickets.getNoOfTickets() *
                TYPE_PRICE_MAP.get(INFANT);
        var adultPrice = adultTickets.getNoOfTickets() * 20;
        var expectedPrice = childPrice + infantPrice + adultPrice;

        sut.purchaseTickets(id, childTickets, infantTickets, adultTickets);

        verify(paymentService).makePayment(id, expectedPrice);
    }

    @Test
    public void givenNoInvalidCase_whenPurchaseTickets_thenCalculateCorrectSeating() {
        var id = 2L;
        var childTickets = new TicketTypeRequest(CHILD, 4);
        var infantTickets = new TicketTypeRequest(INFANT, 2);
        var adultTickets = new TicketTypeRequest(ADULT, 1);
        var eligibleSeating = TicketServiceImpl.ELIGIBLE_SEATING;
        var expected = 0;
        if (eligibleSeating.contains(childTickets.getTicketType()))
            expected += childTickets.getNoOfTickets();
        if (eligibleSeating.contains(infantTickets.getTicketType()))
            expected += infantTickets.getNoOfTickets();
        if (eligibleSeating.contains(adultTickets.getTicketType()))
            expected += adultTickets.getNoOfTickets();

        sut.purchaseTickets(id, childTickets, infantTickets, adultTickets);

        verify(reservationService).reserveSeat(id, expected);
    }

    @Test
    public void whenPurchaseTickets_thenIgnoreNullTypeRequest() {
        var id = 2L;
        var infantTickets = new TicketTypeRequest(INFANT, 4);
        var adultTickets = new TicketTypeRequest(ADULT, 1);
        var infantPrice = infantTickets.getNoOfTickets() *
                TYPE_PRICE_MAP.get(INFANT);
        var adultPrice = adultTickets.getNoOfTickets() *
                TYPE_PRICE_MAP.get(ADULT);
        var eligibleSeating = TicketServiceImpl.ELIGIBLE_SEATING;
        var expectedPrice = infantPrice + adultPrice;
        var expectedSeating = 0;
        if (eligibleSeating.contains(infantTickets.getTicketType()))
            expectedSeating += infantTickets.getNoOfTickets();
        if (eligibleSeating.contains(adultTickets.getTicketType()))
            expectedSeating += adultTickets.getNoOfTickets();

        sut.purchaseTickets(id, null, infantTickets, adultTickets);

        verify(paymentService).makePayment(id, expectedPrice);
        verify(reservationService).reserveSeat(id, expectedSeating);
    }
}
