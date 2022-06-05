package pl.confitura.jelatyna.allegro.adapter;

import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.github.scribejava.core.model.OAuthConstants.REDIRECT_URI;
import static org.springframework.http.HttpHeaders.ACCEPT;

public class AllegroClient {
    private static final String PROTECTED_RESOURCE_URL = "https://api.allegro.pl.allegrosandbox.pl/order/checkout-forms";
    public static final String ALLEGRO_CONTENT_TYPE = "application/vnd.allegro.public.v1+json";

    private final OAuth20Service service;

    public AllegroClient(AllegroProperties context) {
        this.service = new AllegroServiceBuilder(context.getClientId())
                .apiSecret(context.getClientSecret())
                .defaultScope("allegro:api:orders:read allegro:api:orders:write")
                .debug()
                .build(AllegroApi.instance());
    }

    @SuppressWarnings("PMD.SystemPrintln")
    public static void main(String... args) throws IOException, InterruptedException, ExecutionException {
        // Replace these with your client id and secret

        AllegroAuthorizationContext context = new AllegroAuthorizationContext();

        AllegroProperties authorizationContext = new AllegroProperties();
        AllegroClient client = new AllegroClient(authorizationContext);


        Scanner in = new Scanner(System.in, "UTF-8");

        String authorizationUrl = client.getAuthorizationUrl(context.newStateSecret());

        System.out.println(authorizationUrl);

        System.out.print("code >>");
        String code = in.nextLine();
        System.out.println();

        System.out.print("secret >>");
        String stateSecret = in.nextLine();
        System.out.println();

        if (context.validateSecret(stateSecret)) {
            context.setCode(code);
        }

        client.getCheckoutFormsREADY_FOR_PROCESSING(context.getCode());


    }

    public void getCheckoutFormsREADY_FOR_PROCESSING(String code) throws IOException, ExecutionException, InterruptedException {

        OAuth2AccessToken accessToken = getAccessToken(code);
//        accessToken = refreshAccessToken(accessToken.getRefreshToken());

        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        request.addQuerystringParameter("status", "READY_FOR_PROCESSING");
        request.addHeader(ACCEPT, ALLEGRO_CONTENT_TYPE);

        service.signRequest(accessToken, request);

        try (Response response = service.execute(request)) {
            System.out.println(response.getCode());
            System.out.println(response.getBody());
        }

    }

    private OAuth2AccessToken refreshAccessToken(String refreshToken) throws IOException, ExecutionException, InterruptedException {
        return service.refreshAccessToken(refreshToken);
    }

    private OAuth2AccessToken getAccessToken(String code) throws IOException, ExecutionException, InterruptedException {
        Map<String, String> additionalParams = Collections.singletonMap(REDIRECT_URI, "http://localhost:8080");
        return service.getAccessToken(AccessTokenRequestParams.create(code).setExtraParameters(additionalParams));
    }


    public String getAuthorizationUrl(String secretState) {
        Map<String, String> additionalParams = Collections.singletonMap(REDIRECT_URI, "http://localhost:8080");
        return service.createAuthorizationUrlBuilder().state(secretState).additionalParams(additionalParams).build();
    }

}
