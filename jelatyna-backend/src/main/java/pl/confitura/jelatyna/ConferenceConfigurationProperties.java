package pl.confitura.jelatyna;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;

@Data
@ConfigurationProperties("conference")
public class ConferenceConfigurationProperties {

    private C4PConfiguration c4p = new C4PConfiguration();

    @Data
    public static class C4PConfiguration {
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private ZonedDateTime start;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private ZonedDateTime end;

        public boolean isEnabled() {
            ZonedDateTime now = ZonedDateTime.now();
            return now.isAfter(start)
                    && now.isBefore(end);
        }
    }
}
