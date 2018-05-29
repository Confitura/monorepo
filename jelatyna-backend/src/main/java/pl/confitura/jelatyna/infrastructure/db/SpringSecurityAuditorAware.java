package pl.confitura.jelatyna.infrastructure.db;

import org.springframework.data.domain.AuditorAware;
import pl.confitura.jelatyna.infrastructure.security.SecurityContextUtil;

import java.util.Optional;


class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextUtil.getPrincipal().getId());
    }
}
