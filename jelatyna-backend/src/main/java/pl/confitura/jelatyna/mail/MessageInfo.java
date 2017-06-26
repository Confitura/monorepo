package pl.confitura.jelatyna.mail;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageInfo {
    private String email;
    private byte[] ticket;
    private Map<String, String> variables = new HashMap<>();

    public MessageInfo setToken(String token) {
        this.variables.put("token", token);
        return this;
    }

    public boolean hasTicket() {
        return ticket != null;
    }

    public MessageInfo setName(String name) {
        this.variables.put("name", name);
        return this;
    }
}
