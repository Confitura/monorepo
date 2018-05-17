package pl.confitura.jelatyna.registration.ticket;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


@RepositoryRestResource(path = "tickets")
interface TicketRepository extends Repository<Ticket, String> {

    @RestResource(exported = false)
    @PreAuthorize("@security.isAdmin()")
    Ticket save(Ticket token);

    @PreAuthorize("@security.isAdmin()")
    List<Ticket> findAll();

    @RestResource(exported = false)
    @PreAuthorize("@security.isAdmin()")
    boolean existsById(String token);
}
