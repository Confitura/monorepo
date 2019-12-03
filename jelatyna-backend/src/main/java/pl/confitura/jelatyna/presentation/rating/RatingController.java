package pl.confitura.jelatyna.presentation.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
class RatingController {

    private final RatingService ratingService;

    @PostMapping("/presentations/{presentationId}/ratings")
    @PreAuthorize("@security.isAuthenticated()")
    @Transactional
    @ResponseStatus(CREATED)
    public Rate addRating(
            @PathVariable("presentationId") String presentationId,
            @RequestBody @Valid Rate rate) {
        return ratingService.rate(presentationId, rate);
    }

    @PutMapping("/presentations/{presentationId}/ratings/{ratingId}")
    @PreAuthorize("@security.isAuthenticated()")
    @Transactional
    public void updateRating(@PathVariable("presentationId") String presentationId,
                             @PathVariable("ratingId") String ratingId,
                             @RequestBody @Valid Rate rate) {
        ratingService.updateRating(rate.setId(ratingId).setPresentationId(presentationId));
    }


    @GetMapping("/presentations/{presentationId}/ratings")
    public List<Rate> getRating(@PathVariable("presentationId") String presentationId) {
        return ratingService.findForPresentation(presentationId);
    }
}
