package pl.confitura.jelatyna.partner;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PartnerRepository extends Repository<Partner, String> {

    @PreAuthorize("@security.isAdmin()")
    Partner save(Partner partner);

    Partner findById(String id);

    @PreAuthorize("@security.isAdmin()")
    Iterable<Partner> findAll();

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);

    @Query("FROM Partner as p WHERE p.published = true")
    Iterable<Partner> findPublished();


}
