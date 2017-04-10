package pl.confitura.jelatyna;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionMapper {

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Token Expired")
    @ExceptionHandler(Exception.class)
    public String handleTokenExpiration(Exception ex) {
        return "dupa";
    }
}
