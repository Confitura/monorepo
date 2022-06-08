package pl.confitura.jelatyna.allegro.adapter;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.Data;

import java.util.Random;

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
