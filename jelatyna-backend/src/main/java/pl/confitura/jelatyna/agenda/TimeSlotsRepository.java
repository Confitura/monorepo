package pl.confitura.jelatyna.agenda;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@RestResource(path = "time-slots")
public interface TimeSlotsRepository extends Repository<TimeSlot, String> {

    @PreAuthorize("@security.isAdmin()")
    TimeSlot save(TimeSlot timeSlot);

    TimeSlot findOne(String id);

    Iterable<TimeSlot> findAll();

    @PreAuthorize("@security.isAdmin()")
    void delete(String id);

}
