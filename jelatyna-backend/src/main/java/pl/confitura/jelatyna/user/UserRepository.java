package pl.confitura.jelatyna.user;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(path = "users")
public interface UserRepository extends Repository<User, String> {

    User save(User user);

    User findOne(String userId);

    boolean exists(String id);
}
