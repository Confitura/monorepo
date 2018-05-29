package pl.confitura.jelatyna.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityContextUtil {
    public static JelatynaPrincipal getPrincipal() {
        return (JelatynaPrincipal) Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(it -> it instanceof JelatynaPrincipal)
                .orElse(new JelatynaPrincipal());
    }
}
