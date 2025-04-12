package pl.confitura.jelatyna.infrastructure.fakedb;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.confitura.jelatyna.api.model.PresentationRequest;
import pl.confitura.jelatyna.api.model.WorkshopRequest;
import pl.confitura.jelatyna.login.facebook.FacebookService;
import pl.confitura.jelatyna.login.github.GithubService;
import pl.confitura.jelatyna.login.google.GoogleService;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

@Slf4j
@Configuration
@Profile(FAKE_SECURITY)
@RequiredArgsConstructor
public class FakeUsers {

    private static final User FAKE_ADMIN = createFakeAdmin();
    private static final User FAKE_VOLUNTEER = createFakeVolunteer();
    private static final User FAKE_SPEAKER = createFakeSpeaker();

    private static final Map<String, User> bySystem = mapBySystem(FAKE_ADMIN,
            FAKE_VOLUNTEER,
            FAKE_SPEAKER);

    private final UserRepository userRepository;
    private final PresentationRepository presentationRepository;

    private static Map<String, User> mapBySystem(User... users) {
        Map<String, User> map = new HashMap<>();
        for (User user : users) {
            map.put(user.getOrigin(), user);
        }
        log.info("fake users: {}", map);
        return map;
    }

    public User getBySystem(String provider) {
        User user = bySystem.get(provider);
        if (user != null && user.getId() != null) {
            return userRepository.findById(user.getId());
        }
        return user;
    }


    @PostConstruct
    public void createFakeUsers() {
        try {
            userRepository.save(FAKE_ADMIN);
            userRepository.save(FAKE_VOLUNTEER);
            userRepository.save(FAKE_SPEAKER);
            log.info("saved fake users {},{},{}", FAKE_ADMIN, FAKE_VOLUNTEER, FAKE_SPEAKER);
            presentationRepository.save(Presentation.from(new PresentationRequest("test", "test", "description", "begingner", "Polish", new String[]{"java"}), FAKE_SPEAKER, Set.of()));
            presentationRepository.save(Presentation.from(new WorkshopRequest("test", "test", "description", "begingner", "Polish", new String[]{"java"}, false, 12.20, 45, 21), FAKE_SPEAKER, Set.of()));
        } catch (Exception e) {
            log.warn("exception occured", e);
        }
    }

    private static User createFakeAdmin() {

        return new User()
                .setOrigin(GoogleService.SYSTEM)
                .setName("Admin")
                .setEmail("Admin@example.com")
                .setBio("admin bio")
                .setWww("admin.example.com")
                .setAdmin(true)
                .setPrivacyPolicyAccepted(true);
    }

    private static User createFakeVolunteer() {
        return new User()
                .setOrigin(FacebookService.SYSTEM)
                .setName("volunteer")
                .setEmail("volunteer@example.com")
                .setBio("volunteer bio")
                .setWww("volunteer.example.com")
                .setVolunteer(true)
                .setPrivacyPolicyAccepted(true);
    }

    private static User createFakeSpeaker() {
        return new User()
                .setOrigin(GithubService.SYSTEM)
                .setName("Speaker")
                .setEmail("Speaker@example.com")
                .setBio("Speaker bio")
                .setWww("Speaker.example.com")
                .setPrivacyPolicyAccepted(true);
    }
}
