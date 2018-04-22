package pl.confitura.jelatyna.infrastructure.security;

import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import pl.confitura.jelatyna.user.User;

import static java.util.Collections.emptyList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

public class SecurityHelper {

    private static final JelatynaPrincipal ADMIN = new JelatynaPrincipal()
            .setId("ADMIN")
            .setName("Admin Admiński")
            .setAdmin(true);

    public static final PreAuthenticatedAuthenticationToken ADMIN_TOKEN = createToken(ADMIN);

    private static PreAuthenticatedAuthenticationToken createToken(JelatynaPrincipal principal) {
        return new PreAuthenticatedAuthenticationToken(principal, "", emptyList());
    }

    public static RequestPostProcessor admin(){
        return authentication(ADMIN_TOKEN);
    }

    public static RequestPostProcessor user(User user){
        JelatynaPrincipal jelatynaPrincipal = new JelatynaPrincipal()
                .setId(user.getId())
                .setName(user.getName());
        return authentication(createToken(jelatynaPrincipal));
    }
    public static RequestPostProcessor user(String user){
        JelatynaPrincipal jelatynaPrincipal = new JelatynaPrincipal()
                .setId(user)
                .setName(user);
        return authentication(createToken(jelatynaPrincipal));
    }
}
