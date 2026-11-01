package pl.confitura.jelatyna.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.confitura.jelatyna.identity.user.domain.Login;
import pl.confitura.jelatyna.identity.user.domain.LoginRepository;
import pl.confitura.jelatyna.identity.user.domain.User;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;

@Service
public class OAuthUserService {
    private final UserRepository userRepository;
    private final LoginRepository loginRepository;

    public OAuthUserService(UserRepository userRepository, LoginRepository loginRepository) {
        this.userRepository = userRepository;
        this.loginRepository = loginRepository;
    }

    @Transactional
    public User mapToUser(OAuthUserBase oauthUser) {
        return loginRepository.findByProviderAndExternalId(oauthUser.getSystem(), oauthUser.getExternalId())
                .map(Login::getUser)
                .orElseGet(() -> createUserWithLogin(oauthUser));
    }

    private User createUserWithLogin(OAuthUserBase oauthUser) {
        User user = userRepository.save(oauthUser.toUser());
        loginRepository.save(new Login(user, oauthUser.getSystem(), oauthUser.getExternalId()));
        return user;
    }
}
