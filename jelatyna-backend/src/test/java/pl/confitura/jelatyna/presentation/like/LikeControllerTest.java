package pl.confitura.jelatyna.presentation.like;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.presentation.Speaker;
import pl.confitura.jelatyna.user.dto.User;
import pl.confitura.jelatyna.user.UserFacade;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;

@Transactional
class LikeControllerTest extends BaseIntegrationTest {

    @Autowired
    PresentationRepository presentationRepository;

    @Autowired
    UserFacade userFacade;

    @Autowired
    LikeRepository likeRepository;

    private Presentation presentation;

    @BeforeEach
    void setup() {
        this.presentation = createPresentation();
    }

    @Test
    void shouldCreateVote() throws Exception {
        //given user has token
        String token = "token1A";

        //when user likes a presentations
        mockMvc.perform(post("/presentations/" + presentation.getId() + "/likes")
                .content("{\"token\": \"" + token + "\"}")
                .contentType(MediaTypes.HAL_JSON))
                .andExpect(status().is2xxSuccessful());

        //the like is saved
        Like like = likeRepository.findByPresentationAndToken(presentation, token);
        assertThat(like).isNotNull();

    }

    @Test
    void shouldNotAllowSameSecondLikeWithSameToken() throws Exception {
        // given user liked presentation
        String token = "token1A";
        likeRepository.save(new Like().setToken(token).setPresentation(presentation));

        // when user tries to liked second time
        mockMvc.perform(post("/presentations/" + presentation.getId() + "/likes")
                .content("{\"token\": \"" + token + "\"}")
                .contentType(MediaTypes.HAL_JSON))

                // user recieves an error
                .andExpect(status().is4xxClientError());

    }

    @Test
    void shouldAllowToDeleteVote() throws Exception {
        // given user liked presentation
        String token = "token1A";
        Like like = likeRepository.save(new Like().setToken(token).setPresentation(presentation));

        //when user deletes like
        mockMvc.perform(delete("/likes/" + like.getId()))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        //like is deletes
        assertThat(likeRepository.findByPresentationAndToken(presentation, token))
                .isNull();

    }

    @Test
    void adminShouldGetLikesCountForPresentation() throws Exception {
        //given presentation was liked 5 times
        likeRepository.save(new Like().setToken("A").setPresentation(presentation));
        likeRepository.save(new Like().setToken("B").setPresentation(presentation));
        likeRepository.save(new Like().setToken("C").setPresentation(presentation));
        likeRepository.save(new Like().setToken("D").setPresentation(presentation));
        likeRepository.save(new Like().setToken("E").setPresentation(presentation));

        //when admin gets number of likes
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/likes")
                .with(admin()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("5"));
    }

    @Test
    void userShouldNotBeAbleToGetLikes() throws Exception {
        mockMvc.perform(get("/presentations/" + presentation.getId() + "/likes"))
                .andExpect(status().isForbidden());

    }

    @Test
    void adminShouldBeAbleToGetLikesCountForAllPresentations() throws Exception {
        //given few presentations
        List<Presentation> presentations = createPresentations(5);

        //that was rated by few user
        likePresentation(presentations.get(0), 4);
        likePresentation(presentations.get(1), 3);
        likePresentation(presentations.get(2), 4);
        likePresentation(presentations.get(3), 1);
        likePresentation(presentations.get(4), 3);

        //when admin gets all likes

        mockMvc.perform(get("/likes/summary").with(admin()))
                .andExpect(status().is2xxSuccessful())

                //and response should contain calculated ratings
                .andExpect(jsonPath("$." + presentations.get(0).getId()).value("4"))
                .andExpect(jsonPath("$." + presentations.get(1).getId()).value("3"))
                .andExpect(jsonPath("$." + presentations.get(2).getId()).value("4"))
                .andExpect(jsonPath("$." + presentations.get(3).getId()).value("1"))
                .andExpect(jsonPath("$." + presentations.get(4).getId()).value("3"))
        ;


    }

    @Test
    void userShouldNotBeAllowedToGetLikesCountForAllPresentations() throws Exception {
        mockMvc.perform(get("/likes/summary"))
                .andExpect(status().isForbidden());
    }

    @Test
    void userShouldGetLikesIfHeKnowsToken() throws Exception {
        //given user who liked few presentations
        String token = "token1A";
        List<Presentation> presentations = createPresentations(6);
        likePresentation(presentations.get(0), token);
        likePresentation(presentations.get(3), token);
        likePresentation(presentations.get(5), token);


        mockMvc.perform(get("/likes").param("token", token))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].presentationId").value(presentations.get(0).getId()))
                .andExpect(jsonPath("$[1].presentationId").value(presentations.get(3).getId()))
                .andExpect(jsonPath("$[2].presentationId").value(presentations.get(5).getId()));
    }


    private Presentation createPresentation() {
        Presentation presentation = new Presentation();
        presentation.setTitle("title");
        presentation.setShortDescription("shortDescription");
        presentation.setDescription("description");
        presentation.setLevel("level");
        presentation.setLanguage("language");
        User user = userFacade.createUser(new User());
        presentation.setSpeaker(Speaker.fromUser(user));
        return presentationRepository.save(presentation);
    }

    private List<Presentation> createPresentations(int n) {
        return IntStream.range(0, n)
                .mapToObj(it -> createPresentation())
                .collect(toList());
    }

    private void likePresentation(Presentation presentation, int times) {
        for (int i = 0; i < times; i++) {
            likeRepository.save(new Like().setPresentation(presentation).setToken(randomToken()));
        }
    }


    private void likePresentation(Presentation presentation, String token) {
        likeRepository.save(new Like().setPresentation(presentation).setToken(token));
    }

    private String randomToken() {
        return UUID.randomUUID().toString();
    }
}