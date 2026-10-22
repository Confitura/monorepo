package pl.confitura.jelatyna.agenda;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.confitura.jelatyna.agenda.TimeSlot.TimeSlotId;

import java.util.List;

public interface TimeSlotsRepository extends Repository<TimeSlot, String> {

    @PreAuthorize("@security.isAdmin()")
    TimeSlot save(TimeSlot timeSlot);

    TimeSlot findById(TimeSlotId id);

    @Query("select slot from TimeSlot slot order by slot.id.displayOrder")
    List<TimeSlot> findAll();

    @PreAuthorize("@security.isAdmin()")
    void deleteById(TimeSlotId id);

    List<TimeSlot> findByIdDayId(String dayId);

    // A time slot follows its Day's Edition: the admin agenda edits only the Current Edition's (ADR-0004).
    @Query("select slot from TimeSlot slot where slot.id = :id " +
           "and slot.id.dayId in (select day.id from Day day where day.edition.current = true)")
    TimeSlot findInCurrentEditionById(TimeSlotId id);

    @Query("select slot from TimeSlot slot where slot.id.dayId = :dayId " +
           "and slot.id.dayId in (select day.id from Day day where day.edition.current = true)")
    List<TimeSlot> findInCurrentEditionByDayId(String dayId);
}
