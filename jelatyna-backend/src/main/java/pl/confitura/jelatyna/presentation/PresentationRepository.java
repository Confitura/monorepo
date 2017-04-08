package pl.confitura.jelatyna.presentation;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

@RepositoryRestResource(path = "presentations", excerptProjection = InlineTags.class)
public interface PresentationRepository extends Repository<Presentation, String> {

    @RestResource(exported = false)
    Presentation save(Presentation presentation);

    @PreAuthorize("@security.presentationOwnedByUser(#id)")
    void delete(@P("id") String id);

    Presentation findOne(String id);

}
