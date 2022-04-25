package pl.confitura.jelatyna.infrastructure.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.ConferenceConfigurationProperties;

@Component
@RequiredArgsConstructor
class ConferenceInfoContributor implements InfoContributor {
    private final ConferenceConfigurationProperties properties;


    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("c4p", properties.getC4p());
    }
}
