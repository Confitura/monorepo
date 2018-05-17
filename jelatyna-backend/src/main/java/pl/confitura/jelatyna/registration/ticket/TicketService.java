package pl.confitura.jelatyna.registration.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public Ticket generateTicket() {
        return ticketRepository.save(new Ticket());
    }

    public boolean isValid(Ticket ticket) {
        if (ticket == null || ticket.getId() == null) {
            return false;
        } else {
            return ticketRepository.existsById(ticket.getId());
        }
    }
}
