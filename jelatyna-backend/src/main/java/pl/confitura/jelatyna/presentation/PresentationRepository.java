package pl.confitura.jelatyna.presentation;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.confitura.jelatyna.conference.edition.domain.EditionContent;
import pl.confitura.jelatyna.edition.Edition;
import pl.confitura.jelatyna.identity.user.domain.Speakers;
import pl.confitura.jelatyna.user.User;

import java.util.List;
import java.util.Set;

public interface PresentationRepository extends Repository<Presentation, String>, EditionContent, Speakers {

    Presentation save(Presentation presentation);

    @PreAuthorize("@security.presentationOwnedByUser(#id)")
    void deleteById(@P("id") String id);

    Presentation findById(String id);

    // Admin pages show only the Current Edition (ADR-0004).
    @PreAuthorize("@security.isAdmin()")
    @Query("SELECT p FROM Presentation p WHERE p.edition.current = true")
    List<Presentation> findAllInCurrentEdition();

    @PreAuthorize("@security.isAdmin()")
    List<Presentation> findAll();

    // Vote-for-papers covers only the Current Edition's talks (ADR-0004).
    @Query("SELECT p FROM Presentation p WHERE p.edition.current = true")
    Iterable<Presentation> findAllForV4p();

    @Query("SELECT p FROM Presentation p " +
           " left join fetch p.tags " +
           " left join fetch p.speakers " +
           " WHERE p.status ='accepted' ")
    List<Presentation> findAccepted();

    @Query("SELECT p FROM Presentation p " +
           " left join fetch p.tags " +
           " left join fetch p.speakers " +
           " WHERE p.status ='accepted' AND p.edition = :edition")
    List<Presentation> findAcceptedIn(Edition edition);

    Long count();

    @Query("SELECT count(p.id) FROM Presentation p WHERE p.status ='accepted'")
    Long countAccepted();

    @Query("SELECT p FROM Presentation p JOIN p.speakers co WHERE p.status ='accepted' and co = :user")
    List<Presentation> findAcceptedWithCoSpeaker(User user);


    List<Presentation> findBySpeakersContains(User speakers);

    List<Presentation> findBySpeakersContainsAndEdition(User speaker, Edition edition);

    @Override
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Presentation p JOIN p.speakers s WHERE s.id = :userId")
    boolean isSpeaker(String userId);

    @Override
    @Query("SELECT DISTINCT s.id FROM Presentation p JOIN p.speakers s")
    Set<String> speakerIds();

    @Override
    @Query("SELECT DISTINCT s.id FROM Presentation p JOIN p.speakers s WHERE p.status ='accepted'")
    Set<String> acceptedSpeakerIds();
}
