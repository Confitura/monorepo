package pl.confitura.jelatyna.registration.ticket;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.confitura.jelatyna.BaseIntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TicketServiceTest extends BaseIntegrationTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;


    @Test
    void shouldGenerateTicketAndStoreIt() {
        //when admin generates ticket
        Ticket ticket = ticketService.generateTicket();

        //then ticket is stored in db
        assertThat(ticket).isNotNull();
        List<Ticket> storedTickets = ticketRepository.findAll();
        assertThat(storedTickets).contains(ticket);

    }

    @Test
    void generatedTicketShouldBeValid() {
        //when admin generates ticket
        Ticket ticket = ticketService.generateTicket();

        //then ticket should be recognized as valid
        assertThat(ticketService.isValid(ticket)).isTrue();
    }

    @Test
    void emptyTicketShouldNotBeValid() {
        Ticket ticket = new Ticket();

        //when admin checks if empty ticket is valid
        boolean valid = ticketService.isValid(ticket);

        //then ticket should be recognized as invalid
        assertThat(valid).isFalse();

    }
    @Test
    void notGeneratedValueShouldNotBeValid() {
        Ticket ticket = new Ticket("id");

        //when admin checks if empty ticket is valid
        boolean valid = ticketService.isValid(ticket);

        //then ticket should be recognized as invalid
        assertThat(valid).isFalse();

    }
}