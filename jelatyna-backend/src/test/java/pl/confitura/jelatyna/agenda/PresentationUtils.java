package pl.confitura.jelatyna.agenda;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationFacade;
import pl.confitura.jelatyna.presentation.SpeakerEntity;
import pl.confitura.jelatyna.user.dto.FullUserDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PresentationUtils {

    private final PresentationFacade presentationFacade;
    private final UserUtils userUtils;

    List<List<Presentation>> createPresentationsMatrix(List<List<String>> presentationNames) {
        return presentationNames.stream().map(this::createPresentations).collect(Collectors.toList());
    }

    private List<Presentation> createPresentations(List<String> names) {
        return names.stream().map(this::createPresentation).collect(Collectors.toList());
    }

    private Presentation createPresentation(String title) {
        FullUserDto user = userUtils.createUser(title);

        Presentation presentation = new Presentation()
                .setTitle(title)
                .setDescription("description of " + title)
                .setShortDescription("short description of " + title)
                .setLanguage("pl")
                .setLevel("easy")
                .addSpeaker(SpeakerEntity.fromUser(user));

        return presentationFacade.savePresentation(presentation, user.getId());
    }

}
