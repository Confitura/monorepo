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

@ActiveProfiles({"dev", "fake-db"})
class AllegroClientTest extends BaseIntegrationTest {

    @Autowired
    AllegroImportService allegroClient;

    @Autowired
    VoucherService voucherService;

    @Test
    @Disabled("used for testing allegro connection - requires allegro credentials")
    void testAllegroCall() throws IOException, ExecutionException, InterruptedException {
        SecurityHelper.asAdmin();

        System.out.println("allegroClient.getAuthorizationUrl() = " + allegroClient.getAuthorizationUrl());
        System.out.print("paste code here: ");
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine();

        allegroClient.authorize(code, null);
        allegroClient.createVouchersFromAuctions();
    }
}