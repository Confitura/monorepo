package pl.confitura.jelatyna.presentation;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.confitura.jelatyna.user.User;

import java.util.List;
import java.util.Set;

public interface PresentationRepository extends Repository<Presentation, String> {

    Presentation save(Presentation presentation);

    @PreAuthorize("@security.presentationOwnedByUser(#id)")
    void deleteById(@P("id") String id);

    Presentation findById(String id);

    @PreAuthorize("@security.isAdmin()")
    Iterable<Presentation> findAll();

    @Query("FROM Presentation ")
    Iterable<Presentation> findAllForV4p();

    @Query("FROM Presentation WHERE status ='accepted'")
    Iterable<Presentation> findAccepted();

    Long count();

    @Query("SELECT count(p.id) FROM Presentation p WHERE status ='accepted'")
    Long countAccepted();

    @Query("FROM Presentation p JOIN p.speakers co WHERE p.status ='accepted' and co = ?1")
    List<Presentation> findAcceptedWithCoSpeaker(User user);


    List<Presentation> findBySpeakersContains(User speakers);
}
