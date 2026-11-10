package pl.confitura.jelatyna.platform.web;

import java.io.File;
import java.util.List;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static java.util.concurrent.TimeUnit.DAYS;

@Slf4j
@Configuration
@EnableWebMvc
@EnableConfigurationProperties(WebConfiguration.CorsConfigurationProperties.class)
@RequiredArgsConstructor
@OpenAPIDefinition(info = @Info(title = "Jelatyna API", version = "v1"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class WebConfiguration implements WebMvcConfigurer {

    private final CorsConfigurationProperties properties;

    @Value("${resources.path}")
    private String rootPath;
    @Value("${resources.folder}")
    private File folder;

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        final CorsConfiguration config = buildCorsConfiguration();
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>(new CorsFilter(source));
        // Before Spring Security, so responses it writes itself (e.g. the 401 for a rejected token)
        // carry CORS headers too; otherwise the browser hides them from the admin app.
        registration.setOrder(SecurityFilterProperties.DEFAULT_FILTER_ORDER - 1);
        return registration;
    }

    private CorsConfiguration buildCorsConfiguration() {
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(properties.getOrigins());
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        return config;
    }

    @Bean
    WebUtils webUtils(HttpServletRequest request) {
        return new WebUtils(request);
    }

    @Bean
    ResourceSerializer resourceSerializer() {
        return new ResourceSerializer();
    }

    @SneakyThrows
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler(rootPath + "/**")
                .setCacheControl(CacheControl.maxAge(1, DAYS).cachePublic())
                .addResourceLocations("file:///" + folder.getCanonicalPath() + "/");
        log.info("Resource path: {}", rootPath);
        log.info("Resource folder: {}", folder.getCanonicalPath());

    }

    @ConfigurationProperties("app.cors")
    @lombok.Value
    public static class CorsConfigurationProperties {
        List<String> origins;
    }
}
