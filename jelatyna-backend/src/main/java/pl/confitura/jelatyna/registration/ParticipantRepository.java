package pl.confitura.jelatyna.registration;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.confitura.jelatyna.registration.voucher.Voucher;

import java.util.List;

@RepositoryRestResource(path = "participants")
public interface ParticipantRepository extends Repository<Participant, String> {
    @RestResource(exported = false)
    Participant save(Participant participant);

    @PreAuthorize("@security.isAdmin()")
    Iterable<Participant> findAll();

    Participant findById(String id);

    @RestResource(exported = false)
    @Query("FROM Participant WHERE registrationDate IS NULL")
    Iterable<Participant> findAllUnregistered();

    @RestResource(exported = false)
    @Query("FROM Participant WHERE registrationDate IS NOT NULL")
    Iterable<Participant> findAllRegistered();

    @RestResource(exported = false)
    Long count();

    @Query("SELECT count(p.id) FROM Participant p WHERE registrationDate IS NOT NULL")
    @RestResource(exported = false)
    Long countRegistered();

    @Query("SELECT count(p.id) FROM Participant p WHERE arrivalDate IS NOT NULL")
    @RestResource(exported = false)
    Long countArrived();

    @RestResource(exported = false)
    Participant findByVoucher(Voucher voucher);
}
