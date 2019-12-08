package pl.confitura.jelatyna.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.List;
import java.util.Set;

interface UserRepository extends Repository<UserEntity, String> {

    UserEntity save(UserEntity user);

    UserEntity findById(String userId);

    UserEntity findBySocialId(String socialId);

    boolean existsById(String id);

    boolean existsBySocialId(String socialId);

    @Query("FROM UserEntity WHERE isAdmin = true")
    Collection<UserEntity> findAdmins();

    @Query("FROM UserEntity WHERE isVolunteer = true")
    Collection<UserEntity> findVolunteers();


    @PreAuthorize("@security.isAdmin()")
    List<UserEntity> findAll();

    @Query("FROM UserEntity WHERE " +
            "lower(name) like concat('%',lower(:query),'%') OR " +
            "lower(email) like concat('%',lower(:query),'%') OR " +
            "lower(username) like concat('%',lower(:query),'%') ")
    @PreAuthorize("@security.isAdmin()")
    Iterable<UserEntity> find(@Param("query") String query);

    @Query("Select co FROM Presentation p  " +
            "LEFT JOIN p.speakers co " +
            "WHERE p.status ='accepted'")
    Set<UserEntity> findAllAccepted();

    UserEntity findByEmail(String email);

}
