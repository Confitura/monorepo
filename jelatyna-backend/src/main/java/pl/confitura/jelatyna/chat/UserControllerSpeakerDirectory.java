package pl.confitura.jelatyna.chat;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.user.PublicSpeaker;
import pl.confitura.jelatyna.user.UserController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** Speaker directory backed by the existing accepted-speaker query. */
@Component
public class UserControllerSpeakerDirectory implements SpeakerDirectory {

    private final UserController userController;

    public UserControllerSpeakerDirectory(UserController userController) {
        this.userController = userController;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> idToName() {
        Set<PublicSpeaker> speakers = userController.getSpeakers().getBody();
        Map<String, String> map = new HashMap<>();
        if (speakers != null) {
            for (PublicSpeaker speaker : speakers) {
                if (speaker.id() != null && speaker.name() != null && !speaker.name().isBlank()) {
                    map.put(speaker.id(), speaker.name());
                }
            }
        }
        return map;
    }
}
