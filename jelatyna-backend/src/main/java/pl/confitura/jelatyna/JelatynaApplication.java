package pl.confitura.jelatyna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableAsync
@CrossOrigin
@EntityScan(basePackageClasses = JelatynaApplication.class)
public class JelatynaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JelatynaApplication.class, args);
    }

}
