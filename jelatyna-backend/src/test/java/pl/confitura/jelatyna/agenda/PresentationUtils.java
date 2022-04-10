package pl.confitura.jelatyna.agenda;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PresentationUtils {

    private final PresentationRepository presentationRepository;
    private final UserUtils userUtils;

    List<List<Presentation>> createPresentationsMatrix(List<List<String>> presentationNames) {
        return presentationNames.stream().map(this::createPresentations).collect(Collectors.toList());
    }

    private List<Presentation> createPresentations(List<String> names) {
        return names.stream().map(this::createPresentation).collect(Collectors.toList());
    }

    private Presentation createPresentation(String title) {
        User user = userUtils.createUser(title);

        Presentation o = new Presentation()
                .setTitle(title)
                .setDescription("description of "+title)
                .setShortDescription("short description of "+title)
                .setLanguage("pl")
                .setLevel("easy")
                .setSpeaker(user);
        return presentationRepository.save(o);
    }

}
