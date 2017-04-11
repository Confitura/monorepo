package pl.confitura.jelatyna.login;

import org.springframework.stereotype.Service;

import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

@Service
public class OAuthUserService {
    private UserRepository userRepository;

    public OAuthUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User mapToUser(OAuthUserBase oauthUser) {
        String id = oauthUser.encodeId();
        if (!userRepository.exists(id)) {
            userRepository.save(oauthUser.toUser());
        }
        return userRepository.findOne(id);
    }
}
