package pl.confitura.jelatyna.allegro;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.allegro.adapter.AllegroAuthorizationContext;
import pl.confitura.jelatyna.allegro.adapter.AllegroClient;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("allegro")
@RequiredArgsConstructor
class AllegroImportController {
    private final AllegroClient allegroClient;
    private final AllegroAuthorizationContext context;

    @GetMapping("authorize")
    void authorize(HttpServletResponse httpServletResponse) {
        String authorizationUrl = allegroClient.getAuthorizationUrl(context.newStateSecret());
        httpServletResponse.setHeader("Location", authorizationUrl);
        httpServletResponse.setStatus(302);
    }

    @GetMapping("callback")
    void callback(@RequestParam String code, @RequestParam String state) {
        if (context.validateSecret(state)) {
            context.setCode(code);
        }
    }

    @PostMapping("import")
    void importFromAllegro() {

    }
}
