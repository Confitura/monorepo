package com.example;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(SCOPE_SINGLETON)
public class TokenHolder {
    private Map<String, String> tokenToSecret = new HashMap<>();

    public String getSecretFor(String token){
        return tokenToSecret.get(token);
    }

    public void setSecret(String token, String secret){
        tokenToSecret.put(token, secret);
    }

}
