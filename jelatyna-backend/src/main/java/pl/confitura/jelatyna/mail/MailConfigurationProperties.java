package pl.confitura.jelatyna.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "mail")
@Data
public class MailConfigurationProperties {
    private String apiKey;
    private String fromEmail;
    private String fromName;

}
