package pl.confitura.jelatyna.infrastructure.archive;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.agenda.UserUtils;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.news.NewsletterApi;
import pl.confitura.jelatyna.page.PageController;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.PublicProfile;
import pl.confitura.jelatyna.user.UserController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WebpageDataDumperTest extends BaseIntegrationTest {

    @Autowired
    private UserUtils utils;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController userController;

    @Autowired
    private PageController pageController;

    @Autowired
    private NewsletterApi newsletterApi;

    @Autowired
    private PresentationRepository presentationRepository;

    private WebpageDataDumper webpageDataDumper;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        webpageDataDumper = new WebpageDataDumper(
                objectMapper,
                "/tmp/confitura/2025",
                userController,
                pageController,
                newsletterApi,
                presentationRepository
        );
    }

    @Test
    void shouldBumpAdminsList() throws IOException {
        //given
        utils.createUser("some random");
        utils.createAdmin("Jason Asano");
        utils.createAdmin("John Miller");

        //when
        webpageDataDumper.dumpAdmins();

        //then
        var content = Files.readString(Path.of("/tmp/confitura/2025/users/search/admins.json"));
        var actual = objectMapper.readValue(content, List.class);
        var expected = objectMapper.readValue("""
                [
                  {
                    "id": "John Miller's id",
                    "name": "John Miller",
                    "bio": "John Miller's bio",
                    "twitter": "John Miller's twitter",
                    "github": "John Miller's github",
                    "www": "John Miller's www",
                    "photo": "John Miller.png"
                  },
                  {
                    "id": "Jason Asano's id",
                    "name": "Jason Asano",
                    "bio": "Jason Asano's bio",
                    "twitter": "Jason Asano's twitter",
                    "github": "Jason Asano's github",
                    "www": "Jason Asano's www",
                    "photo": "Jason Asano.png"
                  }
                ]""", List.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldDumpPages() throws IOException {
        //given
        SecurityHelper.asAdmin();
        var pageName = "registration-info";
        var pageContent = "# Registration\nWelcome to registration page";
        pageController.createPage(pageName, new PageController.PageContent(pageContent));

        //when
        webpageDataDumper.dumpPages();

        //then
        var content = Files.readString(Path.of("/tmp/confitura/2025/pages/" + pageName + ".json"));
        var actual = objectMapper.readValue(content, String.class);
        assertThat(actual).isEqualTo(pageContent);
    }

    @Test
    void shouldDumpEachSpeakerPublicProfile() throws IOException {
        // given: two users with accepted presentations
        var alice = utils.createUser("Alice");
        var bob = utils.createUser("Bob");

        var p1 = new Presentation()
                .setTitle("T1").setShortDescription("S1").setDescription("D1").setLevel("L1").setLanguage("EN")
                .setSpeaker(alice);
        p1.setAccepted(true);
        presentationRepository.save(p1);

        var p2 = new Presentation()
                .setTitle("T2").setShortDescription("S2").setDescription("D2").setLevel("L2").setLanguage("EN")
                .setSpeaker(bob);
        p2.setAccepted(true);
        presentationRepository.save(p2);

        // when
        webpageDataDumper.dumpSpeakers();

        // then
        var aliceContent = Files.readString(Path.of("/tmp/confitura/2025/users/" + alice.getId() + "/public.json"));
        var bobContent = Files.readString(Path.of("/tmp/confitura/2025/users/" + bob.getId() + "/public.json"));

        var alicePublic = objectMapper.readValue(aliceContent, PublicProfile.class);
        var bobPublic = objectMapper.readValue(bobContent, PublicProfile.class);

        assertThat(alicePublic).isEqualTo(new PublicProfile(alice));
        assertThat(bobPublic).isEqualTo(new PublicProfile(bob));
    }
}
