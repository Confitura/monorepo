package pl.confitura.jelatyna.conference.agenda.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface AgendaRepository extends Repository<AgendaEntry, String> {

    @PreAuthorize("@security.isAdmin()")
    AgendaEntry save(AgendaEntry agendaEntry);

    List<AgendaEntry> findAll();

    AgendaEntry findById(String id);

    // An entry follows its time slot's Day's Edition: the admin agenda edits only the Current Edition's (ADR-0004).
    @Query("from AgendaEntry entry where entry.id = :id " +
           "and entry.timeSlot.id.editionId in (select edition.id from Edition edition where edition.current = true)")
    AgendaEntry findInCurrentEditionById(String id);

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);

    @Query("from AgendaEntry where timeSlot.forAllRooms = true")
    List<AgendaEntry> findEntriesForAllRooms();

    @Query("from AgendaEntry entry where entry.timeSlot.id.editionId = :#{#day.edition.id} and entry.timeSlot.id.dayId = :#{#day.id}")
    List<AgendaEntry> findByDay(Day day);

    List<AgendaEntry> findByTimeSlotId(TimeSlot.TimeSlotId timeSlotId);

    @Query("from AgendaEntry where room.id = :roomId")
    List<AgendaEntry> findByRoomId(String roomId);

    @Query("from AgendaEntry where presentation.id = :presentationId")
    List<AgendaEntry> findByPresentationId(String presentationId);
}
