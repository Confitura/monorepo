package pl.confitura.jelatyna.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("resources")
@Validated
@Valid
public record ResourceConfigurationProperties(
        @NotNull String path,
        @NotNull String folder,
        @NotNull String resourcesBaseUrl) {
}
