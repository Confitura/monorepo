package pl.confitura.jelatyna.adapters.allegro;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.DeviceAuthorization;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

class AllegroService extends OAuth20Service {

    public AllegroService(DefaultApi20 api, String apiKey, String apiSecret, String callback, String defaultScope, String responseType, OutputStream debugStream, String userAgent, HttpClientConfig httpClientConfig, HttpClient httpClient) {
        super(api, apiKey, apiSecret, callback, defaultScope, responseType, debugStream, userAgent, httpClientConfig, httpClient);
    }

    @Override
    protected OAuthRequest createDeviceAuthorizationCodesRequest(String scope) {
        final OAuthRequest request = new OAuthRequest(Verb.POST, getApi().getDeviceAuthorizationEndpoint());
        String authorization = base64(getApiKey() + ":" + getApiSecret());
        request.addHeader("Authorization", "Basic " + authorization);
        request.addParameter(OAuthConstants.CLIENT_ID, getApiKey());
        if (scope != null) {
            request.addParameter(OAuthConstants.SCOPE, scope);
        } else if (getDefaultScope() != null) {
            request.addParameter(OAuthConstants.SCOPE, getDefaultScope());
        }

        logRequestWithParams("Device Authorization Codes", request);

        return request;

    }

    private String base64(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
}
