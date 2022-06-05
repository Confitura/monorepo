package pl.confitura.jelatyna.adapters.allegro;

import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

class AllegroClient {
    private static final String PROTECTED_RESOURCE_URL = "https://api.allegro.pl.allegrosandbox.pl/order/checkout-forms?status=READY_FOR_PROCESSING";
    private static final String NETWORK_NAME = "Allegro";


    @SuppressWarnings("PMD.SystemPrintln")
    public static void main(String... args) throws IOException, InterruptedException, ExecutionException {
        // Replace these with your client id and secret

        final String secretState = "secret" + new Random().nextInt(999_999);
        final OAuth20Service service = new AllegroServiceBuilder(clientId)
                .debug()
                .apiSecret(clientSecret)
                .defaultScope("allegro:api:orders:read") // replace with desired scope
                .build(AllegroApi.instance());


        final Scanner in = new Scanner(System.in, "UTF-8");

        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        // Obtain the Authorization URL
        System.out.println("Fetching the Authorization URL...");
        //pass access_type=offline to get refresh token
        //https://developers.google.com/identity/protocols/OAuth2WebServer#preparing-to-start-the-oauth-20-flow
        final Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("redirect_uri", "http://localhost:8080");
        //force to reget refresh token (if user are asked not the first time)
//        additionalParams.put("prompt", "consent");
        final String authorizationUrl = service.createAuthorizationUrlBuilder()
                .state(secretState)
                .additionalParams(additionalParams)
                .build();
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        final String code = in.nextLine();
        System.out.println();

        System.out.println("And paste the state from server here. We have set 'secretState'='" + secretState + "'.");
        System.out.print(">>");
        final String value = in.nextLine();
        if (secretState.equals(value)) {
            System.out.println("State value does match!");
        } else {
            System.out.println("Ooops, state value does not match!");
            System.out.println("Expected = " + secretState);
            System.out.println("Got      = " + value);
            System.out.println();
        }

        System.out.println("Trading the Authorization Code for an Access Token...");
        OAuth2AccessToken accessToken = service.getAccessToken(AccessTokenRequestParams.create(code).setExtraParameters(additionalParams));
        System.out.println("Got the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");

        System.out.println("Refreshing the Access Token...");
        accessToken = service.refreshAccessToken(accessToken.getRefreshToken());
        System.out.println("Refreshed the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        while (true) {
            System.out.println("Paste fieldnames to fetch (leave empty to get profile, 'exit' to stop example)");
            System.out.print(">>");
            final String query = in.nextLine();
            System.out.println();

            final String requestUrl;
            if ("exit".equals(query)) {
                break;
            } else if (query == null || query.isEmpty()) {
                requestUrl = PROTECTED_RESOURCE_URL;
            } else {
                requestUrl = PROTECTED_RESOURCE_URL + "?fields=" + query;
            }

            final OAuthRequest request = new OAuthRequest(Verb.GET, requestUrl);
            request.addHeader("accept", "application/vnd.allegro.public.v1+json");
            service.signRequest(accessToken, request);
            System.out.println();
            try (Response response = service.execute(request)) {
                System.out.println(response.getCode());
                System.out.println(response.getBody());
            }
            System.out.println();
        }
    }

    /*  curl -X PUT \
  'https://api.allegro.pl/order/checkout-forms/{checkoutFormId}/fulfillment' \
  -H 'Authorization: Bearer {token}' \
  -H 'Accept: application/vnd.allegro.public.v1+json' \
  -H 'Content-Type: application/vnd.allegro.public.v1+json' \
  -d '{
            "status": "SENT"                - przykładowe wartości: NEW (nowe),
                                            PROCESSING (w realizacji), etc.
                                            Pełną listę znajdziesz w naszej dokumentacji.
  }'*/
}
