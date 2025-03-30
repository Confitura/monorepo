package pl.confitura.jelatyna.infrastructure.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.infrastructure.JsonError;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends AuthenticationFilter {

    private final TokenService tokenService;
    private final ObjectMapper mapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            doAuthorize(request);
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (ExpiredJwtException ex) {
            log.error("Error on parsing token", ex);
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            int status = HttpStatus.UNAUTHORIZED.value();
            response.setStatus(status);
            response.getWriter().write(mapper.writeValueAsString(new JsonError().setMessage("Token Expired!").setStatus(status)));
        }
    }

    private void doAuthorize(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            JelatynaPrincipal principal = tokenService.toUser(authorization.replaceFirst("Bearer ", ""));
            SecurityContextHolder.getContext().setAuthentication(
                    new PreAuthenticatedAuthenticationToken(principal, "", getAuthorities(principal))
            );
        }
    }

    private List<GrantedAuthority> getAuthorities(JelatynaPrincipal principal) {
        if (principal.isAdmin()) {
            return Collections.singletonList(new SimpleGrantedAuthority(SecurityConfiguration.ADMIN));
        } else {
            return Collections.emptyList();
        }
    }
}