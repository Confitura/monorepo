package pl.confitura.jelatyna.infrastructure.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.infrastructure.JsonError;
import pl.confitura.jelatyna.user.UserRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends AuthenticationFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            if (doAuthorize(request)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                unauthorized((HttpServletResponse) servletResponse, "User no longer exists!");
            }
        } catch (ExpiredJwtException ex) {
            log.error("Error on parsing token", ex);
            unauthorized((HttpServletResponse) servletResponse, "Token Expired!");
        }
    }

    // Tokens live for months, so the User they name may have been deleted (or merged into
    // another) since; an existence check by primary key is the one lookup per request.
    private boolean doAuthorize(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            var principal = tokenService.toUser(authorization.replaceFirst("Bearer ", ""));
            if (!userRepository.existsById(principal.getId())) {
                log.info("Rejected token of user {} that no longer exists", principal.getId());
                return false;
            }
            var authenticationToken = new PreAuthenticatedAuthenticationToken(principal, "", getAuthorities(principal));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        return true;
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        int status = HttpStatus.UNAUTHORIZED.value();
        response.setStatus(status);
        response.getWriter().write(mapper.writeValueAsString(new JsonError().setMessage(message).setStatus(status)));
    }

    private List<GrantedAuthority> getAuthorities(JelatynaPrincipal principal) {
        if (principal.isAdmin()) {
            return Collections.singletonList(new SimpleGrantedAuthority(SecurityConfiguration.ADMIN));
        } else {
            return Collections.emptyList();
        }
    }
}