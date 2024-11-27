package pl.confitura.jelatyna.infrastructure.fakedb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.confitura.jelatyna.login.facebook.FacebookService;
import pl.confitura.jelatyna.login.github.GithubService;
import pl.confitura.jelatyna.login.google.GoogleService;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_SECURITY;

@Slf4j
@Configuration
@Profile(FAKE_SECURITY)
public class FakeUsers {

    private static final User FAKE_ADMIN = createFakeAdmin();
    private static final User FAKE_VOLUNTEER = createFakeVolunteer();
    private static final User FAKE_SPEAKER = createFakeSpeaker();

    private static final Map<String, User> bySystem = mapBySystem(FAKE_ADMIN,
            FAKE_VOLUNTEER,
            FAKE_SPEAKER);

    @Autowired
    public FakeUsers(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

    private final UserRepository userRepository;

    @PostConstruct
    public void createFakeUsers() {
        try {
            userRepository.save(FAKE_ADMIN);
            userRepository.save(FAKE_VOLUNTEER);
            userRepository.save(FAKE_SPEAKER);
            log.info("saved fake users {},{},{}", FAKE_ADMIN, FAKE_VOLUNTEER, FAKE_SPEAKER);

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
                .setAdmin(true);
    }

    private static User createFakeVolunteer() {
        return new User()
                .setOrigin(FacebookService.SYSTEM)
                .setName("volunteer")
                .setEmail("volunteer@example.com")
                .setBio("volunteer bio")
                .setWww("volunteer.example.com")
                .setVolunteer(true);
    }

    private static User createFakeSpeaker() {
        return new User()
                .setOrigin(GithubService.SYSTEM)
                .setName("Speaker")
                .setEmail("Speaker@example.com")
                .setBio("Speaker bio")
                .setWww("Speaker.example.com");
    }
}
