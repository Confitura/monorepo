package pl.confitura.jelatyna.infrastructure.security;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.presentation.PresentationRepository;

@Component
@RequestScope
@Slf4j
public class Security {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private PresentationRepository presentationRepository;

    public boolean isOwner(String userId) {
        return userId.equals(getUserId());
    }

    public boolean presentationOwnedByUser(String presentationId) {
        String ownerId = presentationRepository.findOne(presentationId).getSpeaker().getId();
        return ownerId.equals(getUserId());
    }

    public boolean isAdmin() {
        return getPrincipal().isAdmin();
    }

    private JelatynaPrincipal getPrincipal() {
        return (JelatynaPrincipal) Optional.ofNullable((Authentication) request.getUserPrincipal())
                .map(Authentication::getPrincipal)
                .orElse(new JelatynaPrincipal());
    }

    private String getUserId() {
        return getPrincipal().getId();
    }
}
