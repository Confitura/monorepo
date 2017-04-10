package pl.confitura.jelatyna;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import pl.confitura.jelatyna.presentation.Tag;
import pl.confitura.jelatyna.presentation.TagRepository;

@SpringBootApplication
@EnableWebMvc
@CrossOrigin
@EntityScan(
        basePackageClasses = { JelatynaApplication.class, Jsr310JpaConverters.class }
)
public class JelatynaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JelatynaApplication.class, args);
    }

    @Bean
    InitializingBean initializingBean(TagRepository repository){
        return () -> {
            List<Tag> tags = Arrays.asList(
                    new Tag("java", "Java"),
                    new Tag("javascript", "JavaScript"),
                    new Tag("scrum", "Scrum"),
                    new Tag("agile", "Agile")
            );
            repository.save(tags);
        };
    }

//    @Autowired
//    public void configureJackson(Jackson2ObjectMapperBuilder builder) {
//        builder.serializers(new ResourceSerializer());
//    }
}
