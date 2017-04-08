package pl.confitura.jelatyna.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(path = "users")
public interface UserRepository extends CrudRepository<User, String> {
    @Override
    User save(User user);

    @Override
    User findOne(String userId);

    @Override
    boolean exists(String id);
}
