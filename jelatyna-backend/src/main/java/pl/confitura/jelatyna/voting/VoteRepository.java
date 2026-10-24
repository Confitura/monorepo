package pl.confitura.jelatyna.voting;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.edition.Edition;

public interface VoteRepository extends Repository<Vote, String> {
    Set<Vote> saveAll(Iterable<Vote> votes);

    Vote findById(String id);

    Iterable<Vote> findAll();

    @Query("SELECT v FROM Vote v WHERE v.presentation.edition.current = true")
    Iterable<Vote> findAllInCurrentEdition();

    // A voter keeps their browser's token across years, so a past Edition's votes must not count.
    @Query("SELECT v FROM Vote v WHERE v.token = :token and v.presentation.edition.current = true order by v.order")
    List<Vote> findAllForTokenInCurrentEdition(String token);

    // IPs only matter while voting is open, so an Edition that stops being current loses them; its votes stay.
    @PreAuthorize("@security.isAdmin()")
    @Modifying
    @Transactional
    @Query("UPDATE Vote v SET v.client = NULL WHERE v.client IS NOT NULL"
            + " AND v.presentation IN (SELECT p FROM Presentation p WHERE p.edition = :edition)")
    int clearVoterIpsIn(Edition edition);
}
