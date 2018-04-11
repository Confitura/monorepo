package pl.confitura.jelatyna.voting;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(path = "votes")
public interface VoteRepository extends Repository<Vote, String> {
    @RestResource(exported = false)
    Set<Vote> save(Iterable<Vote> votes);

    Vote findById(String id);

    @RestResource(exported = false)
    Iterable<Vote> findAll();

    @RestResource(exported = false)
    @Query("FROM Vote WHERE token = ?1")
    Set<Vote> findAllForToken(String token);
}
