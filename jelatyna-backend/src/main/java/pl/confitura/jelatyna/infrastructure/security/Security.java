package pl.confitura.jelatyna.infrastructure.security;

import static pl.confitura.jelatyna.infrastructure.security.SecurityContextUtil.getPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.UserRepository;

@Component
@Slf4j
public class Security {

    private final PresentationRepository presentationRepository;
    private final UserRepository userRepository;

    @Autowired
    public Security(PresentationRepository presentationRepository, UserRepository userRepository) {
        this.presentationRepository = presentationRepository;
        this.userRepository = userRepository;
    }

    public boolean isOwner(String userId) {
        return isAdmin() || userId.equals(getUserId());
    }

    public boolean presentationOwnedByUser(String presentationId) {
        Presentation presentation = presentationRepository.findById(presentationId);
        return presentation.getSpeakers().stream()
                .anyMatch(owner -> owner.getId().equals(getUserId()));
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
