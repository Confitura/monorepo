package pl.confitura.jelatyna.registration;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import pl.confitura.jelatyna.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.confitura.jelatyna.infrastructure.security.SecurityHelper.admin;

class RegistrationUploadControllerTest extends BaseIntegrationTest {


    @Test
    void shouldReportProgress() throws Exception {
        //given
        String content = "" +
                "test1@confitura.pl;1\n" +
                "test5@confitura.pl;5";

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
                .andExpect(jsonPath("$[1].buyerEmail").value("test5@confitura.pl"))
                .andExpect(jsonPath("$[1].requestedCount").value("5"));

    }

    @Test
    void shouldWorkIfOneLineIsNotCorrect() throws Exception {
        //given
        String content = "" +
                "test1@confitura.pl;1\n" +
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
                .andExpect(jsonPath("$[1].successCount").value("0"));

    }

}