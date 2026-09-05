package pl.confitura.jelatyna.partner;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface PartnerRepository extends Repository<Partner, String> {

    @PreAuthorize("@security.isAdmin()")
    Partner save(Partner partner);

    Partner findById(String id);

    @PreAuthorize("@security.isAdmin()")
    List<Partner> findAll();

    @PreAuthorize("@security.isAdmin()")
    void deleteById(String id);

    @Query("SELECT p FROM Partner p WHERE p.published = true")
    List<Partner> findPublished();


}
