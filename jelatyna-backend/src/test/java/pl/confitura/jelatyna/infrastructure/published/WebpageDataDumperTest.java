package pl.confitura.jelatyna.infrastructure.published;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.confitura.jelatyna.BaseIntegrationTest;

class WebpageDataDumperTest extends BaseIntegrationTest {

    @Autowired
    private WebpageDataDumper webpageDataDumper;


    @Test
    void shouldBumpAllWithoutErrors() {
        webpageDataDumper.dumpAll();
    }

}
