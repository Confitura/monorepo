package pl.confitura.jelatyna;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import pl.confitura.jelatyna.login.facebook.FacebookConfigurationProperties;
import pl.confitura.jelatyna.login.github.GitHubConfigurationProperties;
import pl.confitura.jelatyna.login.twitter.TwitterConfigurationProperties;
import pl.confitura.jelatyna.mail.MailConfigurationProperties;
import pl.confitura.jelatyna.presentation.Tag;
import pl.confitura.jelatyna.presentation.TagRepository;

@SpringBootApplication
@EnableWebMvc
@CrossOrigin
@EntityScan(
        basePackageClasses = { JelatynaApplication.class, Jsr310JpaConverters.class }
)
@EnableConfigurationProperties(
        {
                FacebookConfigurationProperties.class,
                GitHubConfigurationProperties.class,
                TwitterConfigurationProperties.class,
                MailConfigurationProperties.class,
        }
)
public class JelatynaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JelatynaApplication.class, args);
    }

    @Bean
    InitializingBean initializingBean(TagRepository repository) {
        return () -> {
            List<Tag> tags = Arrays.asList(
                    new Tag("java", "Java"),
                    new Tag("javascript", "JavaScript"),
                    new Tag("web", "Web"),
                    new Tag("scrum", "Scrum"),
                    new Tag("agile", "Agile"),
                    new Tag("reactive", "Reactive"),
                    new Tag("bigdata", "Big Data"),
                    new Tag("jvm", "JVM"),
                    new Tag("sc", "Software Craftsmanship"),
                    new Tag("could", "Cloud"),
                    new Tag("microservices", "Microservices")
            );
            repository.save(tags);
        };
    }

}
