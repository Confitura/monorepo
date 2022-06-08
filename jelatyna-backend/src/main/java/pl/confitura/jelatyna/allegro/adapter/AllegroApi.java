package pl.confitura.jelatyna.allegro.adapter;

import com.github.scribejava.core.builder.api.DefaultApi20;

class AllegroApi extends DefaultApi20 {

    private final String accessTokenEndpoint;
    private final String authorizationBaseUrl;

    private AllegroApi(String uri) {
        this.accessTokenEndpoint = uri + "/auth/oauth/token";
        this.authorizationBaseUrl = uri + "/auth/oauth/authorize";
    }

    public static AllegroApi instance(String baseUri) {
        return new AllegroApi(baseUri);
    }

    @Override
    public String getAccessTokenEndpoint() {
        return accessTokenEndpoint;
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return authorizationBaseUrl;
    }
}
