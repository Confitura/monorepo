package pl.confitura.jelatyna.infrastructure;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JsonError {
    private String message;
    private int status;

}
