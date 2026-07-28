package pl.confitura.jelatyna.mail;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class MailControllerTest {

    private static final String BODY = """
            {
              "template": "template",
              "messageInfoList": [
                { "email":"email@example.com", "variables":{"testKey":"testValue"} }
              ]
            }""";

    private MailSender mailSender = Mockito.mock(MailSender.class);

    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new MailController(mailSender)).build();

    @Test
    void shouldCallSender() throws Exception {
        //given

        mockMvc.perform(post("/mailing")
                .content(BODY)
                .contentType(MediaType.APPLICATION_JSON));

        Mockito.verify(mailSender).send("template", info());
    }

    @Test
    void shouldLogMaskedRecipientWhenSendFails() throws Exception {
        Mockito.doThrow(new RuntimeException("boom")).when(mailSender).send(Mockito.anyString(), Mockito.any());
        Logger logger = (Logger) LoggerFactory.getLogger(MailController.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        try {
            mockMvc.perform(post("/mailing")
                    .content(BODY)
                    .contentType(MediaType.APPLICATION_JSON));

            String logged = appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .collect(Collectors.joining("\n"));
            assertTrue(logged.contains("e***@example.com"), () -> "expected a masked address, got: " + logged);
            assertFalse(logged.contains("email@example.com"), () -> "recipient address leaked: " + logged);
        } finally {
            logger.detachAppender(appender);
        }
    }

    private MessageInfo info() {
        Map<String, String> v =new HashMap<>();
        v.put("testKey", "testValue");
        return new MessageInfo().setEmail("email@example.com").setVariables(v);
    }
}