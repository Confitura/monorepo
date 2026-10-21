package pl.confitura.jelatyna.news;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface NewsEntryRepository extends Repository<NewsEntry, String> {

    /**
     * The entries public at {@code now}: published and dated no later, newest first. The moment is a
     * parameter because JPQL current_timestamp becomes Postgres localtimestamp, a wall-clock time
     * that is ambiguous in the hour the clocks go back.
     */
    @Query("select e from NewsEntry e where e.published = true and e.date <= :now order by e.date desc")
    List<NewsEntry> findPublicAt(@Param("now") Instant now);
}
