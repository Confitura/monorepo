package pl.confitura.jelatyna.presentation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.List;

interface PresentationRepository extends Repository<Presentation, String> {

    Presentation save(Presentation presentation);

    @PreAuthorize("@security.presentationOwnedByUser(#id)")
    void deleteById(@P("id") String id);

    Presentation findById(String id);

    @PreAuthorize("@security.isAdmin()")
    Iterable<Presentation> findAll();

    @Query("FROM Presentation ")
    List<Presentation> findAllForV4p();

    @Query("FROM Presentation WHERE status ='accepted'")
    Iterable<Presentation> findAccepted();

    Long count();

    @Query("SELECT count(p.id) FROM Presentation p WHERE status ='accepted'")
    Long countAccepted();

    @Query("SELECT count(p.id) FROM Presentation p WHERE status ='accepted' AND p.speakers = s")
    Long countAcceptedWithSpeaker(Speaker s);

    @Query("FROM Presentation p JOIN p.speakers co WHERE p.status ='accepted' and co = ?1")
    List<Presentation> findAcceptedWithCoSpeaker(Speaker user);
}
