package pl.confitura.jelatyna.infrastructure.fakedb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.confitura.jelatyna.login.facebook.FacebookService;
import pl.confitura.jelatyna.login.github.GithubService;
import pl.confitura.jelatyna.login.google.GoogleService;
import pl.confitura.jelatyna.user.UserFacade;
import pl.confitura.jelatyna.user.dto.FullUserDto;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static pl.confitura.jelatyna.infrastructure.Profiles.FAKE_DB;

@Slf4j
@Configuration
@Profile(FAKE_DB)
@RequiredArgsConstructor
public class FakeDbConfig {
    private static String FAKE_ADMIN_ID = "AAAAAAAAAAAAAAAAAAAAAA==";
    private static String FAKE_VOLUNTEER_ID = "BBBBBBBBBBBBBBBBBBBBBB==";
    private static String FAKE_SPEAKER_ID = "CCCCCCCCCCCCCCCCCCCCCC==";

    private static final FullUserDto FAKE_ADMIN = createFakeAdmin();
    private static FullUserDto FAKE_VOLUNTEER = createFakeVolunteer();
    private static FullUserDto FAKE_SPEAKER = createFakeSpeaker();

    private static Map<String, FullUserDto> bySystem = mapBySystem(FAKE_ADMIN,
            FAKE_VOLUNTEER,
            FAKE_SPEAKER);

    private final UserFacade userFacade;

    private static Map<String, FullUserDto> mapBySystem(FullUserDto... users) {
        Map<String, FullUserDto> map = new HashMap<>();
        for (FullUserDto user : users) {
            map.put(user.getOrigin(), user);
        }
        return map;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

    @PostConstruct
    public void createFakeUsers() {
        userFacade.save(FAKE_ADMIN);
        userFacade.save(FAKE_VOLUNTEER);
        userFacade.save(FAKE_SPEAKER);

    }

    private static FullUserDto createFakeAdmin() {

        return new FullUserDto()
                .setId(FAKE_ADMIN_ID)
                .setOrigin(GoogleService.SYSTEM)
                .setSocialId(GoogleService.SYSTEM)
                .setName("Admin")
                .setEmail("Admin@example.com")
                .setBio("admin bio")
                .setWww("admin.example.com")
                .setAdmin(true);
    }

    private static FullUserDto createFakeVolunteer() {
        return new FullUserDto()
                .setId(FAKE_VOLUNTEER_ID)
                .setOrigin(FacebookService.SYSTEM)
                .setSocialId(FacebookService.SYSTEM)
                .setName("volunteer")
                .setEmail("volunteer@example.com")
                .setBio("volunteer bio")
                .setWww("volunteer.example.com")
                .setVolunteer(true);
    }

    private static FullUserDto createFakeSpeaker() {
        return new FullUserDto()
                .setId(FAKE_SPEAKER_ID)
                .setOrigin(GithubService.SYSTEM)
                .setSocialId(GithubService.SYSTEM)
                .setName("Speaker")
                .setEmail("Speaker@example.com")
                .setBio("Speaker bio")
                .setWww("Speaker.example.com");
    }
}
