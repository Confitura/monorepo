package pl.confitura.jelatyna.registration.voucher;

import org.junit.jupiter.api.Test;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoucherImportTest extends BaseIntegrationTest {

    @Test
    void vouchersCannotBeImportedFromAllegro() throws Exception {
        // Asked as an admin, so a 404 means no handler maps the path, not that access was refused.
        mockMvc.perform(get("/allegro/status").with(SecurityHelper.admin()))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/allegro/import").with(SecurityHelper.admin()))
                .andExpect(status().isNotFound());
    }
}
