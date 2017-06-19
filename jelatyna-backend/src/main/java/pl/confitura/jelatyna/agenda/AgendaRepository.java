package pl.confitura.jelatyna.agenda;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@RepositoryRestResource(path = "agenda", excerptProjection = InlineAgenda.class)
public interface AgendaRepository extends Repository<AgendaEntry, String> {

    @PreAuthorize("@security.isAdmin()")
    AgendaEntry save(AgendaEntry agendaEntry);

    Iterable<AgendaEntry> findAll();

    @PreAuthorize("@security.isAdmin()")
    void delete(String id);

}
