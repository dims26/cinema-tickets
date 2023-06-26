package uk.gov.dwp.uc.pairtest.ticketing;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.ticketing.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.ticketing.exception.InvalidPurchaseException;

import java.util.*;

public class TicketServiceImpl implements TicketService {

    public static final Map<TicketTypeRequest.Type, Integer> TYPE_PRICE_MAP;
    public static final Set<TicketTypeRequest.Type> ELIGIBLE_SEATING =
            Set.of(TicketTypeRequest.Type.ADULT, TicketTypeRequest.Type.CHILD);
    public static final int MAX_NUM_TICKETS = 20;
    private static final long MIN_ID_VALUE = 1L;

    static {//Only called once, would normally be set from config otherwise
        final int INFANT_PRICE = 0;
        final int CHILD_PRICE = 10;
        final int ADULT_PRICE = 20;

        TYPE_PRICE_MAP = new EnumMap<>(TicketTypeRequest.Type.class);
        TYPE_PRICE_MAP.put(TicketTypeRequest.Type.INFANT, INFANT_PRICE);
        TYPE_PRICE_MAP.put(TicketTypeRequest.Type.CHILD, CHILD_PRICE);
        TYPE_PRICE_MAP.put(TicketTypeRequest.Type.ADULT, ADULT_PRICE);
    }

    private final TicketPaymentService paymentService;
    private final SeatReservationService reservationService;

    public TicketServiceImpl(TicketPaymentService paymentService,
                             SeatReservationService reservationService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
    }


    /** Handles ticket purchase requests for given account
     * @param accountId Account purchasing tickets
     * @param ticketTypeRequests Ticket purchase request information
     * @throws InvalidPurchaseException if invalid request
     */
    @Override
    public void purchaseTickets(Long accountId,
                                TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if (isAccountIdInvalid(accountId))//check account ID validity
            throw new InvalidPurchaseException("Invalid ID: " + accountId);

        var totalNumTickets = 0;
        var typeToQuantityMap = new HashMap<TicketTypeRequest.Type, Integer>();

        //consolidate ticket request quantities
        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            if (ticketTypeRequest != null && ticketTypeRequest.getNoOfTickets() > 0){//only consider requests with one or more tickets
                totalNumTickets += ticketTypeRequest.getNoOfTickets();
                if (totalNumTickets > MAX_NUM_TICKETS)
                    throw new InvalidPurchaseException("Invalid purchase request: above ticket limit");

                var countedTicketsForType = typeToQuantityMap.getOrDefault(ticketTypeRequest.getTicketType(), 0)
                        + ticketTypeRequest.getNoOfTickets();
                typeToQuantityMap.put(ticketTypeRequest.getTicketType(), countedTicketsForType);
            }
        }

        //check for  adult tickets
        if (!typeToQuantityMap.containsKey(TicketTypeRequest.Type.ADULT))
            throw new InvalidPurchaseException("Invalid purchase request: no adult present");

        //calculate ticket price
        var totalPrice = 0;
        try {
            totalPrice = TicketUtils.calculateTotalPrice(typeToQuantityMap, TYPE_PRICE_MAP);
        } catch (IllegalArgumentException e) {//unexpected issue with price calculation
            throw new InvalidPurchaseException(e.getMessage());
        }
        paymentService.makePayment(accountId, totalPrice);

        //calculate number of seats required
        var totalSeats = 0;

        try {
            totalSeats = TicketUtils.calculateRequiredSeats(typeToQuantityMap, ELIGIBLE_SEATING);
        } catch (IllegalArgumentException e) {//unexpected issue with price calculation
            throw new InvalidPurchaseException(e.getMessage());
        }
        reservationService.reserveSeat(accountId, totalSeats);
    }


    private boolean isAccountIdInvalid(Long accountId) {
        return accountId == null || accountId < MIN_ID_VALUE;
    }
}
