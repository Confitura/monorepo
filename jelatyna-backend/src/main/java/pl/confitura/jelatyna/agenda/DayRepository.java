package pl.confitura.jelatyna.agenda;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.confitura.jelatyna.conference.edition.domain.EditionContent;
import pl.confitura.jelatyna.conference.edition.domain.Edition;

import java.util.List;

public interface DayRepository extends Repository<Day, Day.DayId>, EditionContent {

    @PreAuthorize("@security.isAdmin()")
    Day save(Day day);

    Day findById(Day.DayId id);

    @Query("select day from Day day order by day.displayOrder")
    List<Day> findAll();

    @Query("select day from Day day where day.edition = :edition order by day.displayOrder")
    List<Day> findAllIn(Edition edition);

    // The admin agenda shows and edits only the Current Edition's Days (ADR-0004).
    @Query("select day from Day day where day.edition.current = true order by day.displayOrder")
    List<Day> findAllInCurrentEdition();

    @Query("select day from Day day where day.id = :id and day.edition.current = true")
    Day findInCurrentEditionById(String id);

    @PreAuthorize("@security.isAdmin()")
    void delete(Day day);
}