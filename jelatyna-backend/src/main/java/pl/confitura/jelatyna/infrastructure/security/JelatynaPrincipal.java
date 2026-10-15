package pl.confitura.jelatyna.infrastructure.security;

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
}
