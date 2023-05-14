package pl.confitura.jelatyna.allegro;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.registration.voucher.VoucherService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

/**
 * register app on https://apps.developer.allegro.pl.allegrosandbox.pl/
 * and setup application-dev.yml
 * allegro:
 *   client-id:
 *   client-secret:
 *   callback: "http://localhost:8080/"
 */
@ActiveProfiles({"dev", "fake-db"})
@Disabled("used for testing allegro connection - requires allegro credentials")
class AllegroClientTest extends BaseIntegrationTest {

    @Autowired
    AllegroImportService allegroClient;

    @Autowired
    VoucherService voucherService;

    @Test
    void testAllegroCall() throws IOException, ExecutionException, InterruptedException {
        SecurityHelper.asAdmin();

        System.out.println("allegroClient.getAuthorizationUrl() = " + allegroClient.getAuthorizationUrl());
        System.out.print("paste code here: ");
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine();

        allegroClient.authorize(code, null);
        allegroClient.createVouchersFromAuctions();
    }
}