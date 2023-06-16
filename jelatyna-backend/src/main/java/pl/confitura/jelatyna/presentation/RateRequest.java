package pl.confitura.jelatyna.presentation;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;
import pl.confitura.jelatyna.presentation.rating.Rate;
import pl.confitura.jelatyna.presentation.rating.RateValue;

@Data
@Accessors(chain = true)
@Validated
public class RateRequest {
    private String id;
    private String reviewerToken;
    private RateValue value;
    private String comment;

    public Rate toRate() {
        return new Rate()
                .setId(id)
                .setValue(value)
                .setComment(comment);
    }
}
