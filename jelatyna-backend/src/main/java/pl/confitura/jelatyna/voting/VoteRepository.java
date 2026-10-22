package pl.confitura.jelatyna.voting;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface VoteRepository extends Repository<Vote, String> {
    Set<Vote> saveAll(Iterable<Vote> votes);

    Vote findById(String id);

    Iterable<Vote> findAll();

    // A voter keeps their browser's token across years, so a past Edition's votes must not count.
    @Query("SELECT v FROM Vote v WHERE v.token = :token and v.presentation.edition.current = true order by v.order")
    List<Vote> findAllForTokenInCurrentEdition(String token);
}
