package pl.confitura.jelatyna.login.github;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "github")
public class GitHubConfigurationProperties {
    private String apiKey;
    private String apiSecret;
    private String callback;

}
