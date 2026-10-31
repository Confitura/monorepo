package pl.confitura.jelatyna.conference.rating.web;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;
import pl.confitura.jelatyna.conference.rating.domain.Rate;
import pl.confitura.jelatyna.conference.rating.domain.RateValue;

@Data
@Accessors(chain = true)
@Validated
public class RateRequest {
    private String reviewerToken;
    private int value;
    private String comment;

    public Rate toRate(String reviewerToken) {
        return new Rate()
                .setValue(RateValue.from(value))
                .setComment(comment)
                .setReviewerToken(reviewerToken);
    }
}
