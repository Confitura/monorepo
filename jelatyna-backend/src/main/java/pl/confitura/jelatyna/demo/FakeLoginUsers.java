package pl.confitura.jelatyna.demo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRequest;
import pl.confitura.jelatyna.conference.presentation.domain.WorkshopRequest;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.identity.user.domain.TokenService;
import pl.confitura.jelatyna.identity.login.providers.facebook.FacebookService;
import pl.confitura.jelatyna.identity.login.providers.github.GithubService;
import pl.confitura.jelatyna.identity.login.providers.google.GoogleService;
import pl.confitura.jelatyna.conference.presentation.domain.Presentation;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.identity.user.domain.User;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;

import java.util.Map;
import java.util.Set;

import static pl.confitura.jelatyna.platform.Profiles.FAKE_SECURITY;

@Slf4j
@Configuration
@Profile(FAKE_SECURITY)
@RequiredArgsConstructor
public class FakeLoginUsers {

    private static final User FAKE_ADMIN = createFakeAdmin();
    private static final User FAKE_VOLUNTEER = createFakeVolunteer();
    private static final User FAKE_SPEAKER = createFakeSpeaker();

    // The fake users have no Logins, so fake sign-in picks one by provider.
    private static final Map<String, User> bySystem = Map.of(
            GoogleService.SYSTEM, FAKE_ADMIN,
            FacebookService.SYSTEM, FAKE_VOLUNTEER,
            GithubService.SYSTEM, FAKE_SPEAKER);

    private final UserRepository userRepository;
    private final PresentationRepository presentationRepository;
    private final EditionRepository editionRepository;
    private final TokenService tokenService;

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
            var description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Non consectetur enim aliqua nibh sadipscing consetetur diam dolor aliquyam pariatur suscipit esse adipisici. Elit sea nonummy facilisis invidunt sadipscing elitr gubergren, aute commodi quis est iriure voluptua lorem laborum te eleifend duo, consectetur fugiat ullamco suscipit eros id facilisi assum suscipit veniam eirmod lobortis euismod lobortis proident. Gubergren labore blandit. Id qui placerat aliquyam luptatum qui sit liber, elitr sunt anim delenit cum nonumy aliquip, facilisi incidunt voluptate mollit odio invidunt sit dolor elitr. Laoreet commodo liber lorem nonumy laoreet. Eu luptatum aute.\n";
            var shortDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Congue consectetuer incidunt magna. Nulla gubergren rebum sunt. Luptatum sunt gubergren. Ad nisl soluta.";
            // Without fake-db the database survives restarts, so seed the presentations only once.
            if (presentationRepository.findBySpeakersContains(FAKE_SPEAKER).isEmpty()) {
                Edition edition = editionRepository.findCurrent();
                presentationRepository.save(Presentation.from(new PresentationRequest("test", shortDescription, description, "begingner", "Polish", new String[]{"java"}), FAKE_SPEAKER, Set.of()).setEdition(edition));
                presentationRepository.save(Presentation.from(new WorkshopRequest("test", "test", "description", "begingner", "Polish", new String[]{"java"}, false, 12.20, 45, 21), FAKE_SPEAKER, Set.of()).setEdition(edition));
            }

            String token = tokenService.asToken(FAKE_ADMIN);
            log.info("fake admin token ===================\n{}\n====================", token);
        } catch (Exception e) {
            log.warn("exception occured", e);
        }
    }

    private static User createFakeAdmin() {

        return new User()
                .setId("00000000-0000-0000-0000-000000000001")
                .setName("Admin")
                .setEmail("Admin@example.com")
                .setBio("admin bio")
                .setWww("admin.example.com")
                .setAdmin(true)
                .setPrivacyPolicyAccepted(true);
    }

    private static User createFakeVolunteer() {
        return new User()
                .setId("00000000-0000-0000-0000-000000000002")
                .setName("volunteer")
                .setEmail("volunteer@example.com")
                .setBio("volunteer bio")
                .setWww("volunteer.example.com")
                .setVolunteer(true)
                .setPrivacyPolicyAccepted(true);
    }

    private static User createFakeSpeaker() {
        return new User()
                .setId("00000000-0000-0000-0000-000000000003")
                .setName("Speaker")
                .setEmail("Speaker@example.com")
                .setBio("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Adipisici praesent autem tincidunt, velit iure nisl soluta sea nonumy nisi dolor adipisici placerat culpa iure, assum erat delenit gubergren mazim fugiat quis nihil diam aliqua lorem elitr. Mollit iriure eros facilisi dignissim aliquyam nibh autem est clita eleifend velit proident et zzril commodo qui non rebum nobis eos dolor. Zzril consequat liber. Veniam eum eirmod molestie feugiat. Liber possim minim.\n" +
                        "\n" +
                        "Option no eros wisi sit stet est, vero facilisi dolore iriure invidunt luptatum lorem est congue mollit iriure blandit, autem culpa dolore duo sadipscing commodi in at facilisi pariatur facilisi liber cillum blandit no aliquip magna. Culpa eiusmod sunt cillum wisi placerat. Eu placerat praesent labore placerat feugiat illum sadipscing excepteur wisi consequat congue mollit sint proident enim officia culpa magna id cum. Culpa sadipscing cum.")
                .setWww("Speaker.example.com")
                .setPrivacyPolicyAccepted(true);
    }
}
