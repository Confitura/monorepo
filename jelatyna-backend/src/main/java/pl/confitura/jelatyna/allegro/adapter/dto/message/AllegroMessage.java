package pl.confitura.jelatyna.allegro.adapter.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AllegroMessage {
    private Recipient recipient;
    private String text;

    public static AllegroMessage create(String buyerLogin, String body) {
        return new AllegroMessage(new Recipient(buyerLogin), body);
    }

    @Data
    @AllArgsConstructor
    static class Recipient {
        private String login;
    }
}
