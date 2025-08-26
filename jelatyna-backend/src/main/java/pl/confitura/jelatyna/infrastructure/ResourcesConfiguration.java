package pl.confitura.jelatyna.infrastructure;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Slf4j
@Configuration
@EnableWebMvc
public class ResourcesConfiguration implements WebMvcConfigurer {

    @Value("${resources.path}")
    private String rootPath;
    @Value("${resources.folder}")
    private File folder;


    @SneakyThrows
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler(rootPath + "/**")
                .addResourceLocations("file:///" + folder.getCanonicalPath() + "/");
        log.info("Resource path: {}", rootPath);
        log.info("Resource folder: {}", folder.getCanonicalPath());

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }

}
