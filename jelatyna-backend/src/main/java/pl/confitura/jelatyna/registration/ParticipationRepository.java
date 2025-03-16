package pl.confitura.jelatyna.registration;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PreAuthorize;

import pl.confitura.jelatyna.registration.voucher.Voucher;

public interface ParticipationRepository extends Repository<ParticipationData, String> {
    ParticipationData save(ParticipationData participationData);

    @PreAuthorize("@security.isAdmin()")
    Collection<ParticipationData> findAll();

    ParticipationData findById(String id);

    @Query("FROM ParticipationData WHERE registrationDate IS NULL")
    Collection<ParticipationData> findAllUnregistered();

    @Query("FROM ParticipationData WHERE registrationDate IS NOT NULL")
    Collection<ParticipationData> findAllRegistered();

    Long count();


    @Query("SELECT count(p.id) FROM ParticipationData p WHERE registrationDate IS NOT NULL")
    Long countRegistered();

    @Query("SELECT count(p.id) FROM ParticipationData p WHERE arrivalDate IS NOT NULL")
    Long countArrived();

    ParticipationData findByVoucher(Voucher voucher);


    @Query("SELECT u FROM ParticipationData u" +
            " WHERE u.ticketSendDate IS NULL")
    @PreAuthorize("@security.isAdmin()")
    List<ParticipationData> findUsersToSendTickets();

    @Query("SELECT u FROM ParticipationData u WHERE u.arrivalDate IS NOT NULL")
    @PreAuthorize("@security.isAdmin()")
    List<ParticipationData> findAllPresentOnConference();
}
