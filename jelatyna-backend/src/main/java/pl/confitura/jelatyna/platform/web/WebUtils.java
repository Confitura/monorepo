package pl.confitura.jelatyna.platform.web;

import static java.util.Optional.ofNullable;

import jakarta.servlet.http.HttpServletRequest;

public class WebUtils {

    private HttpServletRequest request;

    public WebUtils(HttpServletRequest request) {
        this.request = request;
    }

    public String getClientIp() {
        return ofNullable(request.getHeader("X-FORWARDED-FOR"))
                .filter(s -> !s.isEmpty())
                .orElse(request.getRemoteAddr());
    }

}
