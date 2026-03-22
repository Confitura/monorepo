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
}
