package pl.confitura.jelatyna.platform.security;

import java.time.Instant;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString(onlyExplicitlyIncluded = true)
@Accessors(chain = true)
public class JelatynaPrincipal {
    @ToString.Include
    public String id;
    public String name;
    @ToString.Include
    public boolean admin;
    @ToString.Include
    public boolean volunteer;
    // When the person signed in; null for tokens issued before it was recorded.
    public Instant authTime;
}
