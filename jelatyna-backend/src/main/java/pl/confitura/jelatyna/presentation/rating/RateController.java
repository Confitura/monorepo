package pl.confitura.jelatyna.presentation.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.presentation.RateRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


@RequiredArgsConstructor
@RestController
public class RateController {

    private final RatingService ratingService;
    private final RateRepository rateRepository;

    @PostMapping("/presentations/{presentationId}/ratings")
    @Transactional
    public ResponseEntity<?> addRating(@PathVariable String presentationId, @RequestBody @Valid @NotNull RateRequest rate) {
        if (rate.getReviewerToken() == null) {
            return ResponseEntity.badRequest().body("Reviewer token is required");
        }
        Rate createdRate = ratingService.rate(presentationId, rate.getReviewerToken(), rate.toRate());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRate);
    }

    @PutMapping("/presentations/{presentationId}/ratings/{ratingId}")
    @Transactional
    public ResponseEntity<?> updateRating(@PathVariable("ratingId") String ratingId, @RequestBody @Valid Rate rate) {
        ratingService.updateRating(rate.setId(ratingId));
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    @GetMapping("/presentations/{presentationId}/ratings")
    public ResponseEntity<?> getRating(@PathVariable("presentationId") String presentationId) {
        List<Rate> rates = rateRepository.findByPresentationId(presentationId);
        return ResponseEntity.status(HttpStatus.OK).body(rates);
    }
}
