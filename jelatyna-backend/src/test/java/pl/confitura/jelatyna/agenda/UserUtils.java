package pl.confitura.jelatyna.agenda;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.user.UserFacade;
import pl.confitura.jelatyna.user.dto.FullUserDto;

@Component
@AllArgsConstructor
public class UserUtils {

    private final UserFacade userFacade;


    public FullUserDto createUser(String title) {
        return userFacade.save(new FullUserDto().setId(title).setName(title));
    }

}
