package pl.confitura.jelatyna.registration.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("registrationTokenService")
@RequiredArgsConstructor
class TokenService {

    private final TokenRepository tokenRepository;

    public Token generateToken() {
        return tokenRepository.save(new Token());
    }

    public boolean isValid(Token token) {
        if (token == null || token.getToken() == null) {
            return false;
        } else {
            return tokenRepository.existsById(token.getToken());
        }
    }
}
