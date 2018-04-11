package pl.confitura.jelatyna.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourcesConfiguration {
    @Component
    class WebConfigurer implements WebMvcConfigurer {
        @Value("${resources.path}")
        private String rootPath;
        @Value("${resources.folder}")
        private String folder;


        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry
                    .addResourceHandler( rootPath + "/**/*")
                    .addResourceLocations("file:///" + folder + "/");
        }

    }
}
