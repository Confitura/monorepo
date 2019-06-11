package pl.confitura.jelatyna.registration;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import pl.confitura.jelatyna.registration.voucher.Voucher;

@RepositoryRestResource(path = "participants")
public interface ParticipationRepository extends Repository<ParticipationData, String> {
    @RestResource(exported = false)
    ParticipationData save(ParticipationData participationData);

    @PreAuthorize("@security.isAdmin()")
    Collection<ParticipationData> findAll();

    @RestResource(exported = false)
    ParticipationData findById(String id);

    @RestResource(exported = false)
    @Query("FROM ParticipationData WHERE registrationDate IS NULL")
    Collection<ParticipationData> findAllUnregistered();

    @RestResource(exported = false)
    @Query("FROM ParticipationData WHERE registrationDate IS NOT NULL")
    Collection<ParticipationData> findAllRegistered();

    @RestResource(exported = false)
    Long count();


    @Query("SELECT count(p.id) FROM ParticipationData p WHERE registrationDate IS NOT NULL")
    @RestResource(exported = false)
    Long countRegistered();

    @Query("SELECT count(p.id) FROM ParticipationData p WHERE arrivalDate IS NOT NULL")
    @RestResource(exported = false)
    Long countArrived();

    @RestResource(exported = false)
    ParticipationData findByVoucher(Voucher voucher);


    @Query("SELECT u FROM ParticipationData u" +
            " WHERE u.ticketSendDate IS NULL")
    @PreAuthorize("@security.isAdmin()")
    List<ParticipationData> findUsersToSendTickets();

    @Query("SELECT u FROM ParticipationData u WHERE u.arrivalDate IS NOT NULL")
    @PreAuthorize("@security.isAdmin()")
    List<ParticipationData> findAllPresentOnConference();
}
