package pl.confitura.jelatyna.allegro.adapter;

import tools.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pl.confitura.jelatyna.allegro.adapter.dto.CheckoutForms;
import pl.confitura.jelatyna.allegro.adapter.dto.message.AllegroMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.github.scribejava.core.model.OAuthConstants.REDIRECT_URI;
import static com.github.scribejava.core.model.Verb.POST;
import static com.github.scribejava.core.model.Verb.PUT;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Slf4j
public class AllegroClient {
    public static final String ALLEGRO_CONTENT_TYPE = "application/vnd.allegro.public.v1+json";

    private final OAuth20Service service;
    private final AllegroAuthorizationContext context = new AllegroAuthorizationContext();
    private final AllegroProperties properties;
    private final ObjectMapper objectMapper;

    public AllegroClient(AllegroProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.service = new AllegroServiceBuilder(properties.getClientId())
                .apiSecret(properties.getClientSecret())
                .defaultScope("allegro:api:orders:read allegro:api:orders:write allegro:api:messaging")
                .build(AllegroApi.instance(properties.getUri()));
        this.objectMapper = objectMapper;
    }

    public void authorize(String code, String stateSecret) {
        if (context.validateSecret(stateSecret) || stateSecret == null) {
            context.setCode(code);
        }
    }

    public CheckoutForms getReadyForProcessing() throws IOException, ExecutionException, InterruptedException {
        final OAuthRequest request = new OAuthRequest(Verb.GET, properties.getApi() + "/order/checkout-forms");
        request.addQuerystringParameter("status", "READY_FOR_PROCESSING");
        request.addQuerystringParameter("fulfillment.status", "NEW");
        request.addHeader(ACCEPT, ALLEGRO_CONTENT_TYPE);
        return executeRequest(request, CheckoutForms.class);
    }

    public void markSent(String checkoutFormId) throws IOException, ExecutionException, InterruptedException {
        String url = properties.getApi() + "/order/checkout-forms/" + checkoutFormId + "/fulfillment";
        final OAuthRequest request = new OAuthRequest(PUT, url);
        request.addHeader(ACCEPT, ALLEGRO_CONTENT_TYPE);
        request.addHeader(CONTENT_TYPE, ALLEGRO_CONTENT_TYPE);
        request.setPayload("{\"status\": \"SENT\"}");
        executeRequest(request);
    }

    private OAuth2AccessToken refreshAccessToken(String refreshToken) throws IOException, ExecutionException, InterruptedException {
        return service.refreshAccessToken(refreshToken);
    }

    private OAuth2AccessToken getAccessToken(AllegroAuthorizationContext context) throws IOException, ExecutionException, InterruptedException {
        try {
            if (!context.hasAccessToken()) {
                Map<String, String> additionalParams = Collections.singletonMap(REDIRECT_URI, properties.getCallback());
                OAuth2AccessToken accessToken = service.getAccessToken(AccessTokenRequestParams.create(context.getCode()).setExtraParameters(additionalParams));
                context.setAccessToken(accessToken);
            }
            return context.getAccessToken();
        } catch (OAuth2AccessTokenErrorResponse ex) {
            context.clear();
            throw new RuntimeException("unable to authorize, try again?", ex);
        }
    }


    public String getAuthorizationUrl() {
        Map<String, String> additionalParams = Collections.singletonMap(REDIRECT_URI, properties.getCallback());
        return service.createAuthorizationUrlBuilder()
                .state(context.newStateSecret())
                .additionalParams(additionalParams)
                .build();
    }

    public boolean isAuthorized() {
        return context.isAuthorized();
    }

    public boolean sendMessage(String login, String testMessage) throws IOException, ExecutionException, InterruptedException {
        log.info("sending Allegro message");
        String url = properties.getApi() + "/messaging/messages";
        final OAuthRequest request = new OAuthRequest(POST, url);
        request.addHeader(ACCEPT, ALLEGRO_CONTENT_TYPE);
        request.addHeader(CONTENT_TYPE, ALLEGRO_CONTENT_TYPE);
        request.setPayload(objectMapper.writeValueAsString(AllegroMessage.create(login, testMessage)));
        return executeRequest(request);
    }


    private <T> T executeRequest(OAuthRequest request, Class<T> responseType) throws IOException, ExecutionException, InterruptedException {
        service.signRequest(getAccessToken(context), request);
        try (Response response = service.execute(request)) {
            logResponse(request, response);
            return objectMapper.readValue(response.getBody(), responseType);
        }
    }

    private boolean executeRequest(OAuthRequest request) throws IOException, ExecutionException, InterruptedException {
        service.signRequest(getAccessToken(context), request);
        try (Response response = service.execute(request)) {
            logResponse(request, response);
            return HttpStatus.valueOf(response.getCode()).is2xxSuccessful();
        }
    }

    private void logResponse(OAuthRequest request, Response response) {
        if (response.getCode() >= 200 && response.getCode() < 300) {
            log.debug("Allegro {} {} -> {}", request.getVerb(), request.getUrl(), response.getCode());
        } else {
            log.warn("Allegro {} {} -> {}: {}", request.getVerb(), request.getUrl(), response.getCode(),
                    describeErrors(response));
        }
    }

    String describeErrors(Response response) {
        try {
            List<AllegroError> errors = objectMapper.readValue(response.getBody(), AllegroErrors.class).errors();
            if (errors == null || errors.isEmpty()) {
                return "no error details";
            }
            return errors.stream().map(AllegroError::describe).collect(Collectors.joining("; "));
        } catch (Exception ex) {
            return "unparseable error body";
        }
    }

    record AllegroErrors(List<AllegroError> errors) {
    }

    record AllegroError(String code, String message, String path) {
        String describe() {
            return path == null ? code + ": " + message : code + ": " + message + " (" + path + ")";
        }
    }
}
