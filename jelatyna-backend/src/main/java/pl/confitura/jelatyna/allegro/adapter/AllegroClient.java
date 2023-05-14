package pl.confitura.jelatyna.allegro.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.allegro.adapter.dto.CheckoutForm;
import pl.confitura.jelatyna.allegro.adapter.dto.CheckoutForms;
import pl.confitura.jelatyna.allegro.adapter.dto.message.AllegroMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

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
                .debug()
                .build(AllegroApi.instance(properties.getUri()));
        this.objectMapper = objectMapper;
    }

    public void authorize(String code, String stateSecret) {
        if (context.validateSecret(stateSecret) || stateSecret == null) {
            context.setCode(code);
        }
    }

    public CheckoutForms getReadyForProcessing() throws IOException, ExecutionException, InterruptedException {

        OAuth2AccessToken accessToken = getAccessToken(context);
//        accessToken = refreshAccessToken(accessToken.getRefreshToken());

        final OAuthRequest request = new OAuthRequest(Verb.GET, properties.getApi() + "/order/checkout-forms");
        request.addQuerystringParameter("status", "READY_FOR_PROCESSING");
        request.addQuerystringParameter("fulfillment.status", "NEW");
        request.addHeader(ACCEPT, ALLEGRO_CONTENT_TYPE);
        service.signRequest(accessToken, request);

        try (Response response = service.execute(request)) {
            log.debug(String.valueOf(response.getCode()));
            log.debug(response.getBody());
            return objectMapper.readValue(response.getBody(), CheckoutForms.class);
        }
    }

    public void markSent(CheckoutForm checkoutForm) throws IOException, ExecutionException, InterruptedException {

        String url = properties.getApi() + "/order/checkout-forms/" + checkoutForm.getId() + "/fulfillment";
        final OAuthRequest request = new OAuthRequest(PUT, url);
        request.addHeader(ACCEPT, ALLEGRO_CONTENT_TYPE);
        request.addHeader(CONTENT_TYPE, ALLEGRO_CONTENT_TYPE);
        request.setPayload("{\"status\": \"SENT\"}");
        service.signRequest(getAccessToken(context), request);

        try (Response response = service.execute(request)) {
            System.out.println(response.getCode());
            System.out.println(response.getBody());
        }
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

    public void sendMessage(String login, String testMessage) throws IOException, ExecutionException, InterruptedException {
        String url = properties.getApi() + "/messaging/messages";
        final OAuthRequest request = new OAuthRequest(POST, url);
        request.addHeader(ACCEPT, ALLEGRO_CONTENT_TYPE);
        request.addHeader(CONTENT_TYPE, ALLEGRO_CONTENT_TYPE);
        request.setPayload(objectMapper.writeValueAsString(AllegroMessage.create(login, testMessage)));
        service.signRequest(getAccessToken(context), request);


        try (Response response = service.execute(request)) {
            System.out.println(response.getCode());
            System.out.println(response.getBody());
        }
    }
}
