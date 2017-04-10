package pl.confitura.jelatyna.infrastructure.security;

import static java.util.Collections.emptyList;
import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@Profile(FAKE_SECURITY)
@Primary
public class FakeJwtAuthenticationFilter extends AuthenticationFilter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        JelatynaPrincipal principal = new JelatynaPrincipal().setId("FAKE").setName("Jack Faker");
        SecurityContextHolder.getContext().setAuthentication(
                new PreAuthenticatedAuthenticationToken(principal, "", emptyList()));

        filterChain.doFilter(servletRequest, servletResponse);

    }
}
