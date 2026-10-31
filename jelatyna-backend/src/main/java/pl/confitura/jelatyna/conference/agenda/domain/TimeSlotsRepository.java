package pl.confitura.jelatyna.conference.agenda.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.confitura.jelatyna.conference.agenda.domain.TimeSlot.TimeSlotId;

import java.util.List;

public interface TimeSlotsRepository extends Repository<TimeSlot, String> {

    @PreAuthorize("@security.isAdmin()")
    TimeSlot save(TimeSlot timeSlot);

    TimeSlot findById(TimeSlotId id);

    @Query("select slot from TimeSlot slot order by slot.id.displayOrder")
    List<TimeSlot> findAll();

    @PreAuthorize("@security.isAdmin()")
    void deleteById(TimeSlotId id);

    @Query("select slot from TimeSlot slot where slot.id.editionId = :#{#day.edition.id} and slot.id.dayId = :#{#day.id}")
    List<TimeSlot> findByDay(Day day);

    // A time slot follows its Day's Edition: the admin agenda edits only the Current Edition's (ADR-0004).
    @Query("select slot from TimeSlot slot where slot.id.dayId = :dayId and slot.id.displayOrder = :displayOrder " +
           "and slot.id.editionId in (select edition.id from Edition edition where edition.current = true)")
    TimeSlot findInCurrentEditionById(String dayId, int displayOrder);

    @Query("select slot from TimeSlot slot where slot.id.dayId = :dayId " +
           "and slot.id.editionId in (select edition.id from Edition edition where edition.current = true)")
    List<TimeSlot> findInCurrentEditionByDayId(String dayId);
}
