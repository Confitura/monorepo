package pl.confitura.jelatyna.registration.token;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.confitura.jelatyna.BaseIntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest extends BaseIntegrationTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;


    @Test
    void shouldGenerateTokenAndStoreIt() {
        //when admin generates token
        Token token = tokenService.generateToken();

        //then token is stored in db
        assertThat(token).isNotNull();
        List<Token> storedTokens = tokenRepository.findAll();
        assertThat(storedTokens).contains(token);

    }

    @Test
    void generatedTokenShouldBeValid() {
        //when admin generates token
        Token token = tokenService.generateToken();

        //then token should be recognized as valid
        assertThat(tokenService.isValid(token)).isTrue();
    }

    @Test
    void emptyTokenShouldNotBeValid() {
        Token token = new Token();

        //when admin checks if empty token is valid
        boolean valid = tokenService.isValid(token);

        //then token should be recognized as invalid
        assertThat(valid).isFalse();

    }
    @Test
    void notGeneratedValueShouldNotBeValid() {
        Token token = new Token("token");

        //when admin checks if empty token is valid
        boolean valid = tokenService.isValid(token);

        //then token should be recognized as invalid
        assertThat(valid).isFalse();

    }
}