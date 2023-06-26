package uk.gov.dwp.uc.pairtest.ticketing;

import uk.gov.dwp.uc.pairtest.ticketing.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.ticketing.exception.InvalidPurchaseException;

public interface TicketService {

    void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException;

}
