package pl.confitura.jelatyna.registration.ticket;

import org.springframework.data.repository.Repository;

import java.util.List;


interface TicketRepository extends Repository<Ticket, String> {

    Ticket save(Ticket token);

    List<Ticket> findAll();

    boolean existsById(String token);
}
