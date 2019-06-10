package pl.confitura.jelatyna.user;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@RestResource(path = "users")
public interface UserRepository extends Repository<User, String> {

    User save(User user);

    @RestResource(exported = false)
    User findById(String userId);

    @RestResource(exported = false)
    User findBySocialId(String socialId);

    boolean existsById(String id);

    @RestResource(exported = false)
    boolean existsBySocialId(String socialId);

    @Query("FROM User WHERE isAdmin = true")
    @RestResource(exported = false)
    Collection<User> findAdmins();

    @Query("FROM User WHERE isVolunteer = true")
    @RestResource(exported = false)
    Collection<User> findVolunteers();


    @PreAuthorize("@security.isAdmin()")
    Iterable<User> findAll();

    @RestResource(path = "byName", rel = "byName")
    @Query("FROM User WHERE " +
            "lower(name) like concat('%',lower(:query),'%') OR " +
            "lower(email) like concat('%',lower(:query),'%') OR " +
            "lower(username) like concat('%',lower(:query),'%') ")
    @PreAuthorize("@security.isAdmin()")
    Iterable<User> find(@Param("query") String query);

    @RestResource(exported = false)
    @Query("Select co FROM Presentation p  " +
            "LEFT JOIN p.speakers co " +
            "WHERE p.status ='accepted'")
    Set<User> findAllAccepted();

    @RestResource(exported = false)
    User findByEmail(String email);

}
