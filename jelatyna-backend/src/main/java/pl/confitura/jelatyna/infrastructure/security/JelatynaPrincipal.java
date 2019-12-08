package pl.confitura.jelatyna.infrastructure.security;

import lombok.Data;

@Data
public class JelatynaPrincipal {
    public String id;
    public String name;
    public boolean admin;
    public boolean volunteer;
}
