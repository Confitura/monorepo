package pl.confitura.jelatyna.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.presentation.PresentationSecurity;

import static pl.confitura.jelatyna.infrastructure.security.SecurityContextUtil.getPrincipal;

@Component
@Slf4j
@RequiredArgsConstructor
public class Security {

    private final PresentationSecurity presentationFacade;

    public boolean isOwner(String userId) {
        return isAdmin() || userId.equals(getUserId());
    }

    public boolean presentationOwnedByUser(String presentationId) {
        return presentationFacade.isUserASpeakerOfPresentation(getUserId(), presentationId);
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
