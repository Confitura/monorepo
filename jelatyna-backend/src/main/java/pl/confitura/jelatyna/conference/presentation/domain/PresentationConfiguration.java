package pl.confitura.jelatyna.conference.presentation.domain;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.confitura.jelatyna.identity.user.domain.Security;

@Configuration
class PresentationConfiguration {

    // Named "presentationSecurity" for the @presentationSecurity checks in @PreAuthorize.
    @Bean
    PresentationSecurity presentationSecurity(PresentationRepository presentationRepository, Security security) {
        return new PresentationSecurity(presentationRepository, security);
    }

    @Bean
    TalkMover talkMover(PresentationRepository presentations) {
        return new TalkMover(presentations);
    }

    @Bean
    InitializingBean initializingBean(TagRepository repository) {
        return () -> {
            List<Tag> tags = Arrays.asList(
                    new Tag("java", "Java"),
                    new Tag("web", "Web"),
                    new Tag("agile", "Agile"),
                    new Tag("reactive", "Reactive"),
                    new Tag("bigdata", "Big Data"),
                    new Tag("jvm", "JVM"),
                    new Tag("sc", "Software Craftsmanship"),
                    new Tag("could", "Cloud"),
                    new Tag("microservices", "Microservices"),
                    new Tag("soft-skills", "Soft skills"),
                    new Tag("devops", "DevOps"),
                    new Tag("security", "Security"),
                    new Tag("ai", "Artificial Intelligence")
            );
            repository.saveAll(tags);
        };
    }
}
