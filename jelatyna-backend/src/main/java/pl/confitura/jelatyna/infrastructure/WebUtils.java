package pl.confitura.jelatyna.infrastructure;

import static java.util.Optional.ofNullable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebUtils {

    private HttpServletRequest request;

    @Autowired
    public WebUtils(HttpServletRequest request) {
        this.request = request;
    }

    public String getClientIp() {
        return ofNullable(request.getHeader("X-FORWARDED-FOR"))
                .filter(s -> !s.isEmpty())
                .orElse(request.getRemoteAddr());
    }

}
