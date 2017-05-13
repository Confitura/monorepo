package pl.confitura.jelatyna.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@RestResource(path = "users")
public interface UserRepository extends Repository<User, String> {

    User save(User user);

    User findOne(String userId);

    boolean exists(String id);

    @Query("FROM User WHERE isAdmin = true")
    @RestResource(path = "admins", rel = "admins")
    Iterable<User> findAdmins();

    @PreAuthorize("@security.isAdmin()")
    Iterable<User> findAll();

    @Query("FROM User WHERE " +
            "lower(name) like :query OR " +
            "lower(email) like :query OR " +
            "lower(username) like :query ")
    Iterable<User> find(@Param("query") String query);
}
