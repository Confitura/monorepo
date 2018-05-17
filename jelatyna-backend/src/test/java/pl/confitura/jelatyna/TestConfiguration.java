package pl.confitura.jelatyna;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Configuration
public class TestConfiguration {

    @Bean
    public MockMvc mockMvc(
            WebApplicationContext context) {
        return webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
}
