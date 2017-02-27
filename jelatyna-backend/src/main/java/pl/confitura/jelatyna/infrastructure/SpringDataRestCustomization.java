package pl.confitura.jelatyna.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import pl.confitura.jelatyna.partner.Partner;
import pl.confitura.jelatyna.user.User;

@Configuration
public class SpringDataRestCustomization extends RepositoryRestConfigurerAdapter {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config
                .exposeIdsFor(Partner.class, User.class);
    }

}
