package pl.confitura.jelatyna.registration.token;

import org.springframework.data.repository.Repository;

interface TokenRepository extends Repository<Token, String> {

    Token save(Token token);
}
