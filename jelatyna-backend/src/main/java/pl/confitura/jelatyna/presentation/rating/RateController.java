package pl.confitura.jelatyna.presentation.rating;


import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RepositoryRestController
@RequiredArgsConstructor
public class RateController {
    private final RatingService ratingService;


    @GetMapping("/presentations/{presentationId}/ratings")
    @Transactional
    public ResponseEntity<?> getRatings(@PathVariable String presentationId) {
        List<Rate> rates = ratingService.findRates(presentationId);
        return ResponseEntity.ok(new Resources<>(rates));
    }

    @PostMapping("/presentations/{presentationId}/ratings")
    @Transactional
    public ResponseEntity<?> addRating(@PathVariable String presentationId, @RequestBody @Valid Rate rate) {
        rate.setPresentationId(presentationId);
        Rate createdRate = ratingService.rate(rate);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdRate);
    }

    @PutMapping("/presentations/{presentationId}/ratings/{ratingId}")
    @Transactional
    public ResponseEntity<?> updateRating(
            @PathVariable("presentationId") String presentationId,
            @PathVariable("ratingId") String ratingId,
            @RequestBody @Valid Rate rate) {
        ratingService.updateRating(rate.setId(ratingId).setPresentationId(presentationId));
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
