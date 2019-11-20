package pl.confitura.jelatyna.login;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import pl.confitura.jelatyna.user.dto.User;
import pl.confitura.jelatyna.user.UserFacade;

@RequiredArgsConstructor
@Service
public class OAuthUserService {

    private final UserFacade userFacade;

    public User mapToUser(OAuthUserBase oauthUser) {
        String id = oauthUser.encodeId();
        if (!userFacade.existsUserWithSocialId(id)) {
            userFacade.createUser(oauthUser.toUser());
        }
        return userFacade.findBySocialId(id);
    }
}
