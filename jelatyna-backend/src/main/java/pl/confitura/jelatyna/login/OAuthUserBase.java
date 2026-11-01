package pl.confitura.jelatyna.login;

import lombok.Data;
import pl.confitura.jelatyna.identity.user.domain.User;

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

}
