package com.example;

import static com.github.scribejava.core.model.Verb.GET;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.oauth.OAuth10aService;

@Service
public class TwitterService {
    @Value("${twitter.api-key}")
    private String apiKey;
    @Value("${twitter.api-secret}")
    private String apiSecret;
    @Value("${twitter.callback}")
    private String callback;
    private OAuth10aService service;

    @PostConstruct
    public void createService() {
        service = new ServiceBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback("http://localhost:8080/")
                .build(TwitterApi.instance());
    }

    public OAuth1RequestToken getRequestToken() {
        try {
            return service.getRequestToken();
        } catch (Exception e) {
            throw new RuntimeException("Exception while loading the token", e);
        }
    }

    public OAuth1AccessToken getAccessToken(OAuth1RequestToken token, String verifier) {
        try {
            return service.getAccessToken(token, verifier);
        } catch (Exception e) {
            throw new RuntimeException("Exception while loading access token", e);
        }
    }

    public UserDto getUser(OAuth1AccessToken accessToken) {
        try {
            return doGetUser(accessToken);
        } catch (Exception e) {
            throw new RuntimeException("Exception while validating a user", e);

        }
    }

    private UserDto doGetUser(OAuth1AccessToken accessToken) throws Exception {
        OAuthRequest request = new OAuthRequest(GET, "https://api.twitter.com/1.1/account/verify_credentials.json");
        service.signRequest(accessToken, request);
        Response response = service.execute(request);
        return mapToUser(response.getBody());
    }

    private UserDto mapToUser(String body) throws java.io.IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(body, UserDto.class);
    }
}

