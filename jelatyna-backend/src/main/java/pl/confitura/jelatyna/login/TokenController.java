package pl.confitura.jelatyna.login;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.confitura.jelatyna.platform.security.JelatynaPrincipal;
import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

@RestController
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @PostMapping("/tokens/refresh-token")
    String refreshToken(@AuthenticationPrincipal JelatynaPrincipal principal) {
        User user = userRepository.findById(principal.id);
        // Refreshing is not signing in, so the sign-in time carries over and an old token can't pass for a fresh one.
        return tokenService.asToken(user, principal.getAuthTime());
    }
}
