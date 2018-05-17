package pl.confitura.jelatyna.presentation.rating;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Data
public class UserAlreadyRatedPresentation extends RuntimeException {
    private final String userId;
    private final String presentationId;

    public UserAlreadyRatedPresentation(String userId, String presentationId) {
        super("User " + userId + " already rated presentation " + presentationId);
        this.userId = userId;
        this.presentationId = presentationId;
    }
}
