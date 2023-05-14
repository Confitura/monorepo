package pl.confitura.jelatyna.allegro;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("allegro")
@RequiredArgsConstructor
class AllegroImportController {
    private final AllegroImportService allegroImportService;

    @GetMapping("status")
    Map<String, Object> getStatus() throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("isAuthorized", allegroImportService.isAuthorized());
        return response;
    }

    @GetMapping("authorize")
    void authorize(HttpServletResponse httpServletResponse) throws IOException {
        String authorizationUrl = allegroImportService.getAuthorizationUrl();
        httpServletResponse.sendRedirect(authorizationUrl);
    }

    @GetMapping("callback")
    String callback(@RequestParam String code, @RequestParam String state) {
        allegroImportService.authorize(code, state);
        return "you are authorized. return to app";
    }

    @GetMapping(value = "report/ready-to-send", produces = {"text/csv"})
    String importFromAllegro() throws IOException, ExecutionException, InterruptedException {
        return allegroImportService.getReadyToSendCsv();

    }

    @PostMapping("import")
    void doImport() throws IOException, ExecutionException, InterruptedException {
        allegroImportService.createVouchersFromAuctions();
    }
}
