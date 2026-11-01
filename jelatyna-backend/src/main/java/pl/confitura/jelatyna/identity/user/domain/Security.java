package pl.confitura.jelatyna.identity.user.domain;

import static pl.confitura.jelatyna.platform.security.SecurityContextUtil.getPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.platform.security.JelatynaPrincipal;
import pl.confitura.jelatyna.user.UserRepository;

@Component("security")
@Slf4j
public class Security {

    private final UserRepository userRepository;

    @Autowired
    public Security(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isOwner(String userId) {
        return isAdmin() || userId.equals(getUserId());
    }

    public boolean isAdmin() {
        return getPrincipal().isAdmin();
    }

    public boolean isVolunteer() {
        return getPrincipal().isVolunteer() || isAdmin();
    }

    public boolean isAuthenticated() {
        JelatynaPrincipal principal = getPrincipal();
        log.info("Logged in user {}", principal);
        return principal.getId() != null;
    }

    public String getUserId() {
        return getPrincipal().getId();
    }
}
