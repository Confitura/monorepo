package pl.confitura.jelatyna.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.Set;

interface UserRepository extends Repository<User, String> {

    User save(User user);

    User findById(String userId);

    User findBySocialId(String socialId);

    boolean existsById(String id);

    boolean existsBySocialId(String socialId);

    @Query("FROM User WHERE isAdmin = true")
    Collection<User> findAdmins();

    @Query("FROM User WHERE isVolunteer = true")
    Collection<User> findVolunteers();


    @PreAuthorize("@security.isAdmin()")
    Iterable<User> findAll();

    @Query("FROM User WHERE " +
            "lower(name) like concat('%',lower(:query),'%') OR " +
            "lower(email) like concat('%',lower(:query),'%') OR " +
            "lower(username) like concat('%',lower(:query),'%') ")
    @PreAuthorize("@security.isAdmin()")
    Iterable<User> find(@Param("query") String query);

    @Query("Select co FROM Presentation p  " +
            "LEFT JOIN p.speakers co " +
            "WHERE p.status ='accepted'")
    Set<User> findAllAccepted();

    User findByEmail(String email);

}
