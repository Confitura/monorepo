package pl.confitura.jelatyna.infrastructure.security;

import static pl.confitura.jelatyna.infrastructure.Profiles.PRODUCTION;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.infrastructure.JsonError;

@Component
@Profile(PRODUCTION)
@Slf4j
public class JwtAuthenticationFilter extends AuthenticationFilter {

    private TokenService tokenService;
    private ObjectMapper mapper;

    @Autowired
    public JwtAuthenticationFilter(TokenService tokenService, ObjectMapper mapper) {
        this.tokenService = tokenService;
        this.mapper = mapper;
    }

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
            SecurityContextHolder.getContext()
                    .setAuthentication(new PreAuthenticatedAuthenticationToken(principal, "",
                            Collections.emptyList()));
        }
    }
}