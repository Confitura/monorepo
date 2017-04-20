package pl.confitura.jelatyna.partner;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@RestResource(path = "partners")
public interface PartnerRepository extends Repository<Partner, String> {

    @PreAuthorize("@security.isAdmin()")
    Partner save(Partner partner);

    Partner findOne(String id);

    Iterable<Partner> findAll();

    @PreAuthorize("@security.isAdmin()")
    void delete(String id);


}
