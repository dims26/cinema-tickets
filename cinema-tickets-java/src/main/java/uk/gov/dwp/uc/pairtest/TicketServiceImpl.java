package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId,
                                TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if (isAccountIdInvalid(accountId))//check account ID validity
            throw new InvalidPurchaseException("Invalid ID: " + accountId);

    }

    private boolean isAccountIdInvalid(Long accountId) {
        return accountId == null || accountId < 1;
    }
}
