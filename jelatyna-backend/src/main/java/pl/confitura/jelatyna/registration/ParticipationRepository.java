package pl.confitura.jelatyna.registration;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.confitura.jelatyna.registration.voucher.Voucher;

@RepositoryRestResource(path = "participants")
public interface ParticipationRepository extends Repository<ParticipapationData, String> {
    @RestResource(exported = false)
    ParticipapationData save(ParticipapationData participapationData);

    @PreAuthorize("@security.isAdmin()")
    Iterable<ParticipapationData> findAll();

    ParticipapationData findById(String id);

    @RestResource(exported = false)
    @Query("FROM ParticipapationData WHERE registrationDate IS NULL")
    Iterable<ParticipapationData> findAllUnregistered();

    @RestResource(exported = false)
    @Query("FROM ParticipapationData WHERE registrationDate IS NOT NULL")
    Iterable<ParticipapationData> findAllRegistered();

    @RestResource(exported = false)
    Long count();

    @Query("SELECT count(p.id) FROM ParticipapationData p WHERE registrationDate IS NOT NULL")
    @RestResource(exported = false)
    Long countRegistered();

    @Query("SELECT count(p.id) FROM ParticipapationData p WHERE arrivalDate IS NOT NULL")
    @RestResource(exported = false)
    Long countArrived();

    @RestResource(exported = false)
    ParticipapationData findByVoucher(Voucher voucher);
}
