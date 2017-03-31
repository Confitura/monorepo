package pl.confitura.jelatyna;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@CrossOrigin
@EntityScan(
        basePackageClasses = { JelatynaApplication.class, Jsr310JpaConverters.class }
)
public class JelatynaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JelatynaApplication.class, args);
    }



//    @Autowired
//    public void configureJackson(Jackson2ObjectMapperBuilder builder) {
//        builder.serializers(new ResourceSerializer());
//    }
}
