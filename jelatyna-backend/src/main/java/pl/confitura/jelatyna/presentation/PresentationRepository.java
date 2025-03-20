package pl.confitura.jelatyna.presentation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.confitura.jelatyna.user.User;

import java.util.List;

@RepositoryRestResource(path = "presentations", excerptProjection = InlineTags.class)
public interface PresentationRepository extends Repository<Presentation, String> {

    @RestResource(exported = false)
    Presentation save(Presentation presentation);

    @PreAuthorize("@security.presentationOwnedByUser(#id)")
    void deleteById(@P("id") String id);

    Presentation findById(String id);

    @PreAuthorize("@security.isAdmin()")
    Iterable<Presentation> findAll();

    @Query("FROM Presentation ")
    @RestResource(exported = false)
    Iterable<Presentation> findAllForV4p();

    @Query("FROM Presentation WHERE status ='accepted'")
    @RestResource(path = "accepted", rel = "accepted")
    Iterable<Presentation> findAccepted();

    @RestResource(exported = false)
    Long count();

    @Query("SELECT count(p.id) FROM Presentation p WHERE status ='accepted'")
    @RestResource(exported = false)
    Long countAccepted();

    @Query("FROM Presentation p JOIN p.speakers co WHERE p.status ='accepted' and co = ?1")
    @RestResource(exported = false)
    List<Presentation> findAcceptedWithCoSpeaker(User user);
}
