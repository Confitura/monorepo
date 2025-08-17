package pl.confitura.jelatyna.agenda;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface AgendaRepository extends Repository<AgendaEntry, String> {

    @PreAuthorize("@security.isAdmin()")
    AgendaEntry save(AgendaEntry agendaEntry);

    Iterable<AgendaEntry> findAll();

    AgendaEntry findById(String id);

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);

    @Query("from AgendaEntry where timeSlot.forAllRooms = true")
    List<AgendaEntry> findEntriesForAllRooms();

    List<AgendaEntry> findByTimeSlotIdDayId(String dayId);
}
