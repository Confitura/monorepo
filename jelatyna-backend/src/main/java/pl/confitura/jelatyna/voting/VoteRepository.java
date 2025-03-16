package pl.confitura.jelatyna.voting;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface VoteRepository extends Repository<Vote, String> {
    Set<Vote> saveAll(Iterable<Vote> votes);

    Vote findById(String id);

    Iterable<Vote> findAll();

    @Query("FROM Vote WHERE token = ?1")
    Set<Vote> findAllForToken(String token);
}
