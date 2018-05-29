package pl.confitura.jelatyna.mail;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class MailControllerTest {

    private MailSender mailSender = Mockito.mock(MailSender.class);

    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new MailController(mailSender)).build();

    @Test
    void shouldCallSender() throws Exception {
        //given

        String body = "{\n" +
                "  \"template\": \"template\",\n" +
                "  \"messageInfoList\": [\n" +
                "    { \"email\":\"email@example.com\", \"variables\":{\"testKey\":\"testValue\"} }\n" +
                "  ]\n" +
                "}";
        mockMvc.perform(post("/mailing")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON));

        Mockito.verify(mailSender).send("template", info());
    }

    private MessageInfo info() {
        Map<String, String> v =new HashMap<>();
        v.put("testKey", "testValue");
        return new MessageInfo().setEmail("email@example.com").setVariables(v);
    }
}