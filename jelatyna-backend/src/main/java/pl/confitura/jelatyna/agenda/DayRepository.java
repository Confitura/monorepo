package pl.confitura.jelatyna.agenda;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.confitura.jelatyna.edition.Edition;

import java.util.List;

public interface DayRepository extends Repository<Day, String> {

    @PreAuthorize("@security.isAdmin()")
    Day save(Day day);

    Day findById(String id);

    @Query("select day from Day day order by day.displayOrder")
    List<Day> findAll();

    boolean existsByEdition(Edition edition);

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);
}