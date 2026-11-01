package pl.confitura.jelatyna.conference.presentation.domain;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pl.confitura.jelatyna.identity.user.domain.Security;

/**
 * The {@code @PreAuthorize} checks over a talk or workshop, as {@code @presentationSecurity}. The user and role
 * checks they combine with stay in identity's {@link Security}.
 */
@Component("presentationSecurity")
@RequiredArgsConstructor
public class PresentationSecurity {

    private final PresentationRepository presentationRepository;
    private final Security security;

    // Lets the talk and workshop endpoints each refuse the other kind, admins included: their
    // updates write different fields. Says nothing about ownership: combine it with an owner check.
    public boolean isWorkshop(String presentationId) {
        return presentationRepository.findById(presentationId).isWorkshop();
    }

    public boolean presentationOwnedByUser(String presentationId) {
        if (security.isAdmin()) {
            return true;
        }
        Presentation presentation = presentationRepository.findById(presentationId);
        return presentation.getSpeakers().stream()
                .anyMatch(owner -> owner.getId().equals(security.getUserId()));
    }

    // A past Edition's talks and workshops are read-only for speakers; admins can still change them
    // (ADR-0004). Says nothing about ownership: combine it with an owner check.
    public boolean canChangePresentation(String presentationId) {
        return security.isAdmin() || presentationRepository.findById(presentationId).getEdition().isCurrent();
    }
}
