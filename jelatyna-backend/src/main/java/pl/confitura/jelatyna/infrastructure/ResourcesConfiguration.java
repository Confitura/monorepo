package pl.confitura.jelatyna.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class ResourcesConfiguration {
    @Component
    class WebConfigurer extends WebMvcConfigurerAdapter {
        @Value("${resources.path}")
        private String rootPath;
        @Value("${resources.folder}")
        private String folder;
        @Value("${servlet.contextPath}")
        private String contextPath;

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry
                    .addResourceHandler(contextPath + "/" + rootPath + "/**/*")
                    .addResourceLocations("file:///" + folder + "/");
        }

    }
}
