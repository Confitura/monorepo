package pl.confitura.jelatyna.agenda;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.user.UserFacade;
import pl.confitura.jelatyna.user.dto.User;

@Component
@AllArgsConstructor
public class UserUtils {

    private final UserFacade userFacade;


    public User createUser(String title) {
        return userFacade.save(new User().setId(title).setName(title));
    }

}
