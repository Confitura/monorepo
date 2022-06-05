package pl.confitura.jelatyna.adapters.allegro;

import com.github.scribejava.core.builder.api.DefaultApi20;

class AllegroApi extends DefaultApi20 {
    public static AllegroApi instance() {
        return new AllegroApi();
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://allegro.pl.allegrosandbox.pl/auth/oauth/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://allegro.pl.allegrosandbox.pl/auth/oauth/authorize";
    }

    @Override
    public String getDeviceAuthorizationEndpoint() {
        return "https://allegro.pl.allegrosandbox.pl/auth/oauth/device";
    }
}
