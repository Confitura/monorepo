package pl.confitura.jelatyna.infrastructure.security;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JelatynaPrincipal {
    public String id;
    public String name;
    public Boolean admin;
}
