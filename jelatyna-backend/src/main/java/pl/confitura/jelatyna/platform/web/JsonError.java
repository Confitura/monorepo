package pl.confitura.jelatyna.platform.web;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JsonError {
    private String message;
    private int status;

}
