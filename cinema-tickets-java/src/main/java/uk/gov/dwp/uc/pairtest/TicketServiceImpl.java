package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.HashMap;

public class TicketServiceImpl implements TicketService {
    private static final int MAX_NUM_TICKETS = 20;

    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId,
                                TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if (isAccountIdInvalid(accountId))//check account ID validity
            throw new InvalidPurchaseException("Invalid ID: " + accountId);

        var totalTickets = 0;
        var typeAmountMap = new HashMap<TicketTypeRequest.Type, Integer>();

        //consolidate ticket request quantities
        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            if (ticketTypeRequest.getNoOfTickets() > 0){//only consider requests with one or more tickets
                updateQuantityMap(typeAmountMap, ticketTypeRequest);

                totalTickets += ticketTypeRequest.getNoOfTickets();
                if (totalTickets > MAX_NUM_TICKETS)
                    throw new InvalidPurchaseException("Invalid purchase request: above ticket limit");
            }
        }

        //check for  adult tickets
        if (!typeAmountMap.containsKey(TicketTypeRequest.Type.ADULT))
            throw new InvalidPurchaseException("Invalid purchase request: no adult present");
    }

    private void updateQuantityMap(HashMap<TicketTypeRequest.Type, Integer> map,
                                   TicketTypeRequest request) {
        var countedTickets = map
                .getOrDefault(request.getTicketType(), 0);
        map.put(
                request.getTicketType(),
                countedTickets + request.getNoOfTickets()
        );
    }


    private boolean isAccountIdInvalid(Long accountId) {
        return accountId == null || accountId < 1;
    }
}
