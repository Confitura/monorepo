package pl.confitura.jelatyna.edition;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface EditionRepository extends Repository<Edition, String> {

    @PreAuthorize("@security.isAdmin()")
    Edition save(Edition edition);

    @Query("SELECT e FROM Edition e WHERE e.current = true")
    Edition findCurrent();

    Edition findById(String id);

    boolean existsByYear(int year);

    List<Edition> findAllByOrderByYearDesc();

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);
}
