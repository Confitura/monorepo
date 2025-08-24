package pl.confitura.jelatyna.infrastructure.fakedb;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.confitura.jelatyna.api.model.PresentationRequest;
import pl.confitura.jelatyna.login.github.GithubService;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import java.util.Set;

import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_DB;

@Slf4j
@Configuration
@Profile(FAKE_DB)
@RequiredArgsConstructor
public class FakePresentations {
    private final static Faker faker = new Faker();

    private final UserRepository userRepository;
    private final PresentationRepository presentationRepository;


    @PostConstruct
    public void createFakeUsers() {
        for (int i = 0; i < 40; i++) {
            var u = speaker(i);
            presentation(u);
        }
        for (int i = 40; i < 100; i++) {
            var u = speaker(i);
            var p = presentation(u);
            p.setAccepted(true);
            presentationRepository.save(p);
        }
    }

    private User speaker(int i) {

        String fullName = faker.name().fullName();
        User user = new User()
                .setId("00000000-0000-0000-%d-000000000000".formatted(i))
                .setOrigin(GithubService.SYSTEM)
                .setName(fullName)
                .setEmail(fullName.replace(" ", ".") + "@example.com")
                .setBio(faker.text().text())
                .setWww(fullName.replace(" ", ".") + ".example.com")
                .setPhoto("https://picsum.photos/id/" + i + "/200/300")
                .setPrivacyPolicyAccepted(true);
        return userRepository.save(user);
    }

    private Presentation presentation(User user) {
        Presentation presentation = Presentation.from(new PresentationRequest(
                faker.movie().name(),
                faker.movie().quote(),
                faker.brooklynNineNine().quotes(),
                "begingner",
                "Polish", new String[]{"java"}), user, Set.of());
        return presentationRepository.save(presentation);
    }
}
