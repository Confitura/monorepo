package pl.confitura.jelatyna.registration;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import pl.confitura.jelatyna.BaseIntegrationTest;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;

class RegistrationUploadControllerTest extends BaseIntegrationTest {


    @Test
    void shouldReportProgress() throws Exception {
        //given
        String content = "" +
                "test1@confitura.pl;1;SPONSOR;test\n" +
                "test5@confitura.pl;5;SPEAKER\n" +
                "test2@confitura.pl;2";

        MockMultipartFile file = new MockMultipartFile("file", content.getBytes());

        //when
        ResultActions resultActions = mockMvc.perform(
                multipart("/participants/upload")
                        .file(file)
                        .with(admin()));

        //then
        resultActions
                .andDo(it -> System.out.println(it.getResponse().getContentAsString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].buyerEmail").value("test1@confitura.pl"))
                .andExpect(jsonPath("$[0].requestedCount").value("1"))
                .andExpect(jsonPath("$[0].type").value("SPONSOR"))
                .andExpect(jsonPath("$[0].comment").value("test"))
                .andExpect(jsonPath("$[1].buyerEmail").value("test5@confitura.pl"))
                .andExpect(jsonPath("$[1].requestedCount").value("5"))
                .andExpect(jsonPath("$[1].type").value("SPEAKER"))
                .andExpect(jsonPath("$[1].comment").value(nullValue()))
                .andExpect(jsonPath("$[2].buyerEmail").value("test2@confitura.pl"))
                .andExpect(jsonPath("$[2].requestedCount").value("2"))
                .andExpect(jsonPath("$[2].type").value("PARTICIPANT"))
                .andExpect(jsonPath("$[2].comment").value(nullValue()));

    }

    @Test
    void shouldWorkIfOneLineIsNotCorrect() throws Exception {
        //given
        String content = "" +
                "test1@confitura.pl;1\n" +
                "test1@confitura.pl;1;blah\n" +
                "not a correct entry";

        MockMultipartFile file = new MockMultipartFile("file", content.getBytes());

        //when
        ResultActions resultActions = mockMvc.perform(
                multipart("/participants/upload")
                        .file(file)
                        .with(admin()));

        //then
        resultActions
                .andDo(it -> System.out.println(it.getResponse().getContentAsString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].buyerEmail").value("test1@confitura.pl"))
                .andExpect(jsonPath("$[0].requestedCount").value("1"))
                .andExpect(jsonPath("$[1].buyerEmail").value("ERROR"))
                .andExpect(jsonPath("$[1].requestedCount").value("-1"))
                .andExpect(jsonPath("$[1].successCount").value("0"))
                .andExpect(jsonPath("$[2].buyerEmail").value("ERROR"))
                .andExpect(jsonPath("$[2].requestedCount").value("-1"))
                .andExpect(jsonPath("$[2].successCount").value("0"));

    }

}
