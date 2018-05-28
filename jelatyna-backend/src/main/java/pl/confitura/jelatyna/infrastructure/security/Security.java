package pl.confitura.jelatyna.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import java.util.Objects;

import static pl.confitura.jelatyna.infrastructure.security.SecurityContextUtil.getPrincipal;

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
        String ownerId = presentationRepository.findById(presentationId).getSpeaker().getId();
        return ownerId.equals(getUserId());
    }

    public boolean isAdmin() {
        return getPrincipal().isAdmin();
    }

    public boolean isVolunteer() {
        return getPrincipal().isVolunteer() || isAdmin();
    }

    public boolean isUserAnOwnerOfParticipationData(String participationId) {
        String userId = getUserId();
        User user = userRepository.findById(userId);
        return user.getParticipapationData() != null && Objects.equals(user.getParticipapationData().getId(), participationId);
    }
    public String getUserId() {
        return getPrincipal().getId();
    }
}
