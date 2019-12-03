package pl.confitura.jelatyna.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PresentationSecurity {

    private final PresentationRepository repository;

    public boolean isUserASpeakerOfPresentation(String userId, String presentationId) {
        Presentation presentation = repository.findById(presentationId);
        return presentation.ownerHasId(userId);
    }

}
