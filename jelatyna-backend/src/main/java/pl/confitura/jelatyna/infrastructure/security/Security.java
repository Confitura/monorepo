package pl.confitura.jelatyna.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.presentation.PresentationRepository;

import java.util.Optional;

@Component
@Slf4j
public class Security {
    @Autowired
    private PresentationRepository presentationRepository;

    public boolean isOwner(String userId) {
        return isAdmin() || userId.equals(getUserId());
    }

    public boolean presentationOwnedByUser(String presentationId) {
        String ownerId = presentationRepository.findById(presentationId).getSpeaker().getId();
        return ownerId.equals(getUserId());
    }

    public boolean isAdmin() {
        return getPrincipal().isAdmin();
    }

    public boolean isVolunteer() {
        return getPrincipal().isVolunteer() || isAdmin();
    }

    private JelatynaPrincipal getPrincipal() {
        return (JelatynaPrincipal) Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .orElse(new JelatynaPrincipal());
    }

    public String getUserId() {
        return getPrincipal().getId();
    }
}
