package pl.confitura.jelatyna.agenda;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

public interface DayRepository extends Repository<Day, String> {

    @PreAuthorize("@security.isAdmin()")
    Day save(Day day);

    Day findById(String id);

    @Query("select day from Day day order by displayOrder")
    Iterable<Day> findAll();

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);
}