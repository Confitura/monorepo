package pl.confitura.jelatyna.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString(onlyExplicitlyIncluded = true)
@Accessors(chain = true)
public class MessageInfo {
    private String email;
    private byte[] ticket;
    private Map<String, String> variables = new HashMap<>();

    public MessageInfo setToken(String token) {
        this.variables.put("token", token);
        return this;
    }

    @ToString.Include
    public boolean hasTicket() {
        return ticket != null;
    }

    // Keys only — the values hold the recipient's name and link token.
    @ToString.Include(name = "variables")
    Set<String> variableKeys() {
        return variables == null ? Set.of() : variables.keySet();
    }

    @ToString.Include
    public String maskedEmail() {
        if (email == null) {
            return "<no address>";
        }
        int at = email.indexOf('@');
        return at < 1 ? "***" : email.charAt(0) + "***" + email.substring(at);
    }

    public MessageInfo setName(String name) {
        this.variables.put("name", name);
        return this;
    }
}
