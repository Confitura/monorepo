package pl.confitura.jelatyna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
@EntityScan(
        basePackageClasses = { JelatynaApplication.class, Jsr310JpaConverters.class }
)
public class JelatynaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JelatynaApplication.class, args);
    }


}
