package pl.confitura.jelatyna.adapters.allegro;

import com.github.scribejava.core.builder.ScopeBuilder;
import com.github.scribejava.core.builder.ServiceBuilderOAuth10a;
import com.github.scribejava.core.builder.ServiceBuilderOAuth20;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.utils.Preconditions;

import java.io.OutputStream;

class AllegroServiceBuilder implements ServiceBuilderOAuth10a, ServiceBuilderOAuth20 {

    private String callback;
    private String apiKey;
    private String apiSecret;
    private String scope;
    private OutputStream debugStream;
    private String responseType = "code";
    private String userAgent;

    private HttpClientConfig httpClientConfig;
    private HttpClient httpClient;

    public AllegroServiceBuilder(String apiKey) {
        apiKey(apiKey);
    }

    @Override
    public AllegroServiceBuilder callback(String callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public final AllegroServiceBuilder apiKey(String apiKey) {
        Preconditions.checkEmptyString(apiKey, "Invalid Api key");
        this.apiKey = apiKey;
        return this;
    }

    @Override
    public AllegroServiceBuilder apiSecret(String apiSecret) {
        Preconditions.checkEmptyString(apiSecret, "Invalid Api secret");
        this.apiSecret = apiSecret;
        return this;
    }

    @Override
    public AllegroServiceBuilder apiSecretIsEmptyStringUnsafe() {
        apiSecret = "";
        return this;
    }

    private AllegroServiceBuilder setScope(String scope) {
        Preconditions.checkEmptyString(scope, "Invalid OAuth scope");
        this.scope = scope;
        return this;
    }

    @Override
    public ServiceBuilderOAuth20 defaultScope(String defaultScope) {
        return setScope(defaultScope);
    }

    @Override
    public ServiceBuilderOAuth20 defaultScope(ScopeBuilder scopeBuilder) {
        return setScope(scopeBuilder.build());
    }

    @Override
    public ServiceBuilderOAuth10a withScope(String scope) {
        return setScope(scope);
    }

    @Override
    public AllegroServiceBuilder debugStream(OutputStream debugStream) {
        Preconditions.checkNotNull(debugStream, "debug stream can't be null");
        this.debugStream = debugStream;
        return this;
    }

    @Override
    public ServiceBuilderOAuth20 responseType(String responseType) {
        Preconditions.checkEmptyString(responseType, "Invalid OAuth responseType");
        this.responseType = responseType;
        return this;
    }

    @Override
    public AllegroServiceBuilder httpClientConfig(HttpClientConfig httpClientConfig) {
        Preconditions.checkNotNull(httpClientConfig, "httpClientConfig can't be null");
        this.httpClientConfig = httpClientConfig;
        return this;
    }

    @Override
    public AllegroServiceBuilder httpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    @Override
    public AllegroServiceBuilder userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    @Override
    public AllegroServiceBuilder debug() {
        return debugStream(System.out);
    }

    @Override
    public OAuth10aService build(DefaultApi10a api) {
        throw new RuntimeException("unsupported api");
    }

    @Override
    public OAuth20Service build(DefaultApi20 api) {
        return new AllegroService(api, apiKey, apiSecret, callback, scope, responseType, debugStream, userAgent, httpClientConfig, httpClient);
    }
}
