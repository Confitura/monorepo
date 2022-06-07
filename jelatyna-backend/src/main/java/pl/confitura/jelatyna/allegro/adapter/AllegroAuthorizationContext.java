package pl.confitura.jelatyna.allegro.adapter;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.Data;

import java.util.Random;

/*  curl -X PUT \
'https://api.allegro.pl/order/checkout-forms/{checkoutFormId}/fulfillment' \
-H 'Authorization: Bearer {token}' \
-H 'Accept: application/vnd.allegro.public.v1+json' \
-H 'Content-Type: application/vnd.allegro.public.v1+json' \
-d '{
        "status": "SENT"                - przykładowe wartości: NEW (nowe),
                                        PROCESSING (w realizacji), etc.
                                        Pełną listę znajdziesz w naszej dokumentacji.
}'*/
@Data
class AllegroAuthorizationContext {

    private String stateSecret;
    private String code;
    private OAuth2AccessToken accessToken;

    public String newStateSecret() {
        this.stateSecret = "secret" + new Random().nextInt(999_999);
        return this.stateSecret;
    }

    public boolean validateSecret(String value) {
        if (stateSecret.equals(value)) {
            return true;
        } else {
            System.out.println("Ooops, state value does not match!");
            System.out.println("Expected = " + stateSecret);
            System.out.println("Got      = " + value);
            System.out.println();
            return false;
        }
    }

    public boolean hasAccessToken() {
        return accessToken != null;
    }

    public boolean isAuthorized() {
        return code!=null;
    }

    public void clear() {
        this.accessToken = null;
        this.code = null;
    }
}
