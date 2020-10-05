package pl.confitura.jelatyna.presentation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Stream;

@RepositoryRestResource(path = "presentations", excerptProjection = InlineTags.class)
interface PresentationRepository extends Repository<PresentationEntity, String> {

    @RestResource(exported = false)
    PresentationEntity save(PresentationEntity presentation);

    @PreAuthorize("@security.presentationOwnedByUser(#id)")
    void deleteById(@P("id") String id);

    PresentationEntity findById(String id);

    @PreAuthorize("@security.isAdmin()")
    Iterable<PresentationEntity> findAll();

    @Query("FROM PresentationEntity ")
    @RestResource(exported = false)
    Stream<PresentationEntity> findAllForV4p();

    @Query("FROM PresentationEntity WHERE status ='accepted'")
    @RestResource(path = "accepted", rel = "accepted")
    Iterable<PresentationEntity> findAccepted();

    @RestResource(exported = false)
    Long count();

    @Query("SELECT count(p.id) FROM PresentationEntity p WHERE status ='accepted'")
    @RestResource(exported = false)
    Long countAccepted();

    @Query("SELECT count(p.id) FROM PresentationEntity p WHERE status ='accepted' AND p.speakers = s")
    @RestResource(exported = false)
    Long countAcceptedWithSpeaker(SpeakerEntity s);

    @Query("FROM PresentationEntity p JOIN p.speakers co WHERE p.status ='accepted' and co = ?1")
    @RestResource(exported = false)
    List<PresentationEntity> findAcceptedWithCoSpeaker(SpeakerEntity user);

    //TODO
    @Query("select case when (count(p) > 0)  then true else false end from PresentationEntity p where p.id = ?1 AND p.speakers = ?2")
    boolean isPresentationOwnByUser(String presentationId, String userId);
}
