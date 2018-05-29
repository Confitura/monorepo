package pl.confitura.jelatyna.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class Security {
    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private UserRepository userRepository;

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

    public static JelatynaPrincipal getPrincipal() {
        return (JelatynaPrincipal) Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(it -> it instanceof JelatynaPrincipal)
                .orElse(new JelatynaPrincipal());
    }

    public boolean userRegisteredAsParticipant(String participantId) {
        String userId = getUserId();
        User user = userRepository.findById(userId);
        return user.getParticipant() != null && Objects.equals(user.getParticipant().getId(), participantId);
    }
    public String getUserId() {
        return getPrincipal().getId();
    }
}
