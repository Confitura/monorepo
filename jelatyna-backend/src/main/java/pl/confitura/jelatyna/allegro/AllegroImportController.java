package pl.confitura.jelatyna.allegro;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.allegro.adapter.AllegroClient;
import pl.confitura.jelatyna.allegro.adapter.dto.CheckoutForms;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("allegro")
@RequiredArgsConstructor
class AllegroImportController {
    private final AllegroClient allegroClient;

    @GetMapping("authorize")
    void authorize(HttpServletResponse httpServletResponse) throws IOException {
        String authorizationUrl = allegroClient.getAuthorizationUrl();
        httpServletResponse.sendRedirect(authorizationUrl);
    }

    @GetMapping("callback")
    String callback(@RequestParam String code, @RequestParam String state) {
        allegroClient.authorize(code, state);
        return "you are authorized. return to app";
    }

    //    @PostMapping("import")
    @GetMapping("import")
    String importFromAllegro() throws IOException, ExecutionException, InterruptedException {
        CheckoutForms readyForProcessing = allegroClient.getReadyForProcessing();
        return readyForProcessing.getCheckoutForms().stream().map(
                it -> it.getBuyer().getEmail() + ";" + it.getQuantity()
        ).collect(Collectors.joining("\n"));

    }
}
