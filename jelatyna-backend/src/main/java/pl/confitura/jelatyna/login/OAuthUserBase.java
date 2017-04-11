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

    public String encodeId() {
        return Base64.getEncoder().encodeToString((system + "/" + id).getBytes());
    }

}
