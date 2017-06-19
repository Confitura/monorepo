package pl.confitura.jelatyna.agenda;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@RepositoryRestResource(path = "time-slots", excerptProjection = InlineTimeSlot.class)
public interface TimeSlotsRepository extends Repository<TimeSlot, String> {

    @PreAuthorize("@security.isAdmin()")
    TimeSlot save(TimeSlot timeSlot);

    TimeSlot findOne(String id);

    Iterable<TimeSlot> findAll();

    @PreAuthorize("@security.isAdmin()")
    void delete(String id);

}
