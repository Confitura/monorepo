package pl.confitura.jelatyna.voting;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface VoteRepository extends Repository<Vote, String> {
    Set<Vote> saveAll(Iterable<Vote> votes);

    Vote findById(String id);

    Iterable<Vote> findAll();

    @Query("FROM Vote v WHERE v.token = ?1 order by v.order")
    List<Vote> findAllForToken(String token);
}
