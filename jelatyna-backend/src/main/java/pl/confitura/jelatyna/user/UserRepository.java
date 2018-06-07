package pl.confitura.jelatyna.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestResource(path = "users")

public interface UserRepository extends Repository<User, String> {

    User save(User user);

    User findById(String userId);

    @RestResource(exported = false)
    User findBySocialId(String socialId);

    boolean existsById(String id);

    @RestResource(exported = false)
    boolean existsBySocialId(String socialId);

    @Query("FROM User WHERE isAdmin = true")
    @RestResource(path = "admins", rel = "admins")
    Iterable<User> findAdmins();

    @Query("FROM User WHERE isVolunteer = true")
    @RestResource(path = "volunteers", rel = "volunteers")
    Iterable<User> findVolunteers();

    @PreAuthorize("@security.isAdmin()")
    Iterable<User> findAll();

    @RestResource(path = "byName", rel = "byName")
    @Query("FROM User WHERE " +
            "lower(name) like concat('%',lower(:query),'%') OR " +
            "lower(email) like concat('%',lower(:query),'%') OR " +
            "lower(username) like concat('%',lower(:query),'%') ")
    Iterable<User> find(@Param("query") String query);

    @RestResource(exported = false)
    @Query("Select p.speaker, co FROM Presentation p  " +
            "LEFT JOIN p.cospeakers co " +
            "WHERE p.status ='accepted'")
    Iterable<Object[]> findAllAccepted();

    @RestResource(exported = false)
    User findByEmail(String email);

    @Query("SELECT u FROM User u" +
            " WHERE u.participationData.ticketSendDate IS NULL" +
            " AND u.participationData.voucher IS NOT NULL")
    List<User> findUsersToSendTickets();

    @Query("SELECT u FROM User u WHERE u.participationData.arrivalDate IS NOT NULL")
    List<User> findAllPresentOnConference();

    @RestResource(exported = false)
    @Query("SELECT u FROM User u WHERE u.participationData IS NOT NULL")
    List<User> findParticipants();
}
