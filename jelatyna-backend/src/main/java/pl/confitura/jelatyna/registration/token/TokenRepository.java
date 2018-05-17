package pl.confitura.jelatyna.registration.token;

import org.springframework.data.repository.Repository;

import java.util.List;


interface TokenRepository extends Repository<Token, String> {

    Token save(Token token);

    List<Token> findAll();

    boolean existsById(String token);
}
