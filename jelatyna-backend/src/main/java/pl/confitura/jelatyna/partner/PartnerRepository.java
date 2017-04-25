package pl.confitura.jelatyna.partner;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@RestResource(path = "partners")
public interface PartnerRepository extends Repository<Partner, String> {

    @PreAuthorize("@security.isAdmin()")
    Partner save(Partner partner);

    Partner findOne(String id);

    @PreAuthorize("@security.isAdmin()")
    Iterable<Partner> findAll();

    @PreAuthorize("@security.isAdmin()")
    void delete(String id);

    @Query("FROM Partner as p WHERE p.published = true")
    @RestResource(path = "published", rel = "published")
    Iterable<Partner> findPublished();


}
