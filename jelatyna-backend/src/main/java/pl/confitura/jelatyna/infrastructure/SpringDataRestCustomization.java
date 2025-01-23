package pl.confitura.jelatyna.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

import pl.confitura.jelatyna.page.Page;
import pl.confitura.jelatyna.partner.Partner;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.Tag;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.registration.voucher.Voucher;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.voting.Vote;

@Configuration
public class SpringDataRestCustomization {

    @Bean
    public RepositoryRestConfigurer exposeIdsRepositoryRestConfigurer() {
        return RepositoryRestConfigurer.withConfig(
                (config, corsRegistry) -> config
                        .exposeIdsFor(
                                Partner.class,
                                User.class,
                                Tag.class,
                                Presentation.class,
                                Vote.class,
                                Voucher.class,
                                ParticipationData.class,
                                Page.class)
        );

    }


    @Bean
    public SpelAwareProxyProjectionFactory projectionFactory() {
        return new SpelAwareProxyProjectionFactory();
    }
}
