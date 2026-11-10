package pl.confitura.jelatyna.conference.registration.mandrill;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MailConfigurationProperties.class)
class MandrillConfiguration {

    @Bean
    MandrillMailSender mandrillMailSender(MailConfigurationProperties properties) {
        return new MandrillMailSender(properties);
    }
}
