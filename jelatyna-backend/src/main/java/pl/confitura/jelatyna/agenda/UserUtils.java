package pl.confitura.jelatyna.agenda;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

@Component
@AllArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;

    public User createUser(String title) {
        return userRepository.save(new User().setId(title).setName(title));
    }
}
