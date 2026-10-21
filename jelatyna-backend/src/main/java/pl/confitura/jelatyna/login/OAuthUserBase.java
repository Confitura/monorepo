package pl.confitura.jelatyna.login;

import java.util.Base64;

import lombok.Data;
import pl.confitura.jelatyna.user.User;

@Data
public abstract class OAuthUserBase {
    private String id;
    private String system;

    protected abstract User toUser();

    protected OAuthUserBase(String system) {
        this.system = system;
    }

    /** The provider's id for the person, as a Login stores it. */
    public String getExternalId() {
        return id;
    }

    public String encodeId() {
        return Base64.getEncoder().encodeToString((system + "/" + getExternalId()).getBytes());
    }

}
