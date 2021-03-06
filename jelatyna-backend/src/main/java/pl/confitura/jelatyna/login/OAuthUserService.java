package pl.confitura.jelatyna.login;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import pl.confitura.jelatyna.user.dto.FullUserDto;
import pl.confitura.jelatyna.user.UserFacade;

@RequiredArgsConstructor
@Service
public class OAuthUserService {

    private final UserFacade userFacade;

    public FullUserDto mapToUser(OAuthUserBase oauthUser) {
        String id = oauthUser.encodeId();
        if (!userFacade.existsUserWithSocialId(id)) {
            userFacade.save(oauthUser.toUser());
        }
        return userFacade.findBySocialId(id);
    }
}
