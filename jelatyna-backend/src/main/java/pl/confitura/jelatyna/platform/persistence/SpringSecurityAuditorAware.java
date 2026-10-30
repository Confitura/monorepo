package pl.confitura.jelatyna.platform.persistence;

import org.springframework.data.domain.AuditorAware;
import pl.confitura.jelatyna.platform.security.SecurityContextUtil;

import java.util.Optional;


class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextUtil.getPrincipal().getId());
    }
}
