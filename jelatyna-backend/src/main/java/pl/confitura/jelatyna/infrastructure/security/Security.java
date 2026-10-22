package pl.confitura.jelatyna.infrastructure.security;

import static pl.confitura.jelatyna.infrastructure.security.SecurityContextUtil.getPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.UserRepository;

@Component("security")
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

    // Lets the talk and workshop endpoints each refuse the other kind, admins included: their
    // updates write different fields. Says nothing about ownership: combine it with an owner check.
    public boolean isWorkshop(String presentationId) {
        return presentationRepository.findById(presentationId).isWorkshop();
    }

    public boolean presentationOwnedByUser(String presentationId) {
        if (isAdmin()) {
            return true;
        }
        Presentation presentation = presentationRepository.findById(presentationId);
        return presentation.getSpeakers().stream()
                .anyMatch(owner -> owner.getId().equals(getUserId()));
    }

    // A past Edition's talks and workshops are read-only for speakers; admins can still change them
    // (ADR-0004). Says nothing about ownership: combine it with an owner check.
    public boolean canChangePresentation(String presentationId) {
        return isAdmin() || presentationRepository.findById(presentationId).getEdition().isCurrent();
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
