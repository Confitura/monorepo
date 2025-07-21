package pl.confitura.jelatyna.infrastructure.archive;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.agenda.UserUtils;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.page.PageController;
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

    private WebpageDataDumper webpageDataDumper;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        webpageDataDumper = new WebpageDataDumper(
                objectMapper,
                "/tmp/confitura/2025",
                userController,
                pageController
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
        pageController.createPage(pageName, pageContent);

        //when
        webpageDataDumper.dumpPages();

        //then
        var content = Files.readString(Path.of("/tmp/confitura/2025/pages/" + pageName + ".json"));
        var actual = objectMapper.readValue(content, String.class);
        assertThat(actual).isEqualTo(pageContent);
    }


}