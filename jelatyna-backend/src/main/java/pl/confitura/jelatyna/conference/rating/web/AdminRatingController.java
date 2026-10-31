package pl.confitura.jelatyna.conference.rating.web;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.confitura.jelatyna.conference.rating.domain.ViewPresentationRate;
import pl.confitura.jelatyna.conference.rating.domain.ViewPresentationRateRepository;

import java.util.List;

// The tag this endpoint had while AdminPresentationController served it, so openapi.json stays the same.
@Tag(name = "admin-presentation-controller")
@RequiredArgsConstructor
@RestController
@SecurityRequirement(name = "bearerAuth")
public class AdminRatingController {

    private final ViewPresentationRateRepository ratesRepository;

    @PreAuthorize("@security.isAdmin()")
    @PostMapping("/ratings")
    @Transactional
    public List<ViewPresentationRate> rates() {
        return this.ratesRepository.findAllInCurrentEdition();
    }
}
