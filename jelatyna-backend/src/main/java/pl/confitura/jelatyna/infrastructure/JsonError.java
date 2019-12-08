package pl.confitura.jelatyna.infrastructure;

import lombok.Data;

@Data
public class JsonError {
    private String message;
    private int status;

}
