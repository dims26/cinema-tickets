package service;

import uk.gov.dwp.uc.pairtest.TicketServiceImpl;

public class TicketServiceImplTest extends TicketServiceTest<TicketServiceImpl>{
    @Override
    TicketServiceImpl getImpl() {
        return new TicketServiceImpl();
    }
}
