package pl.confitura.jelatyna.infrastructure.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

public class JwtAuthenticationFilter extends GenericFilterBean {

    private TokenService tokenService;

    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            JelatynaPrincipal principal = tokenService.toUser(authorization.replaceFirst("Bearer ", ""));
            SecurityContextHolder.getContext()
                    .setAuthentication(new PreAuthenticatedAuthenticationToken(principal, "",
                            Collections.emptyList()));
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}