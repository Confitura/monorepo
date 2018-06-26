package pl.confitura.jelatyna.presentation.rating;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
class UserRatingPresentationHaveNotArrived extends RuntimeException {
}
