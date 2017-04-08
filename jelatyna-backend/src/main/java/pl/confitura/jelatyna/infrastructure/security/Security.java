package pl.confitura.jelatyna.infrastructure.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.extern.slf4j.Slf4j;

@Component
@RequestScope
@Slf4j
public class Security {

    @Autowired
    private HttpServletRequest request;

    public boolean isOwner(String userId) {
        return userId.equals(getPrincipal().getId());
    }

    private JelatynaPrincipal getPrincipal() {
        return (JelatynaPrincipal) ((Authentication) request.getUserPrincipal()).getPrincipal();
    }
}
