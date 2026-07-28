package pl.confitura.jelatyna.allegro.adapter;

import com.github.scribejava.core.model.Response;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AllegroClientErrorTest {

    private final AllegroClient client = new AllegroClient(properties(), new ObjectMapper());

    private static AllegroProperties properties() {
        AllegroProperties properties = new AllegroProperties();
        properties.setClientId("client-id");
        properties.setClientSecret("client-secret");
        return properties;
    }

    @Test
    void shouldDescribeAllegroErrorEnvelope() {
        String body = """
                {"errors":[
                  {"code":"NotFound","message":"Checkout form not found","path":"checkoutForm",
                   "userMessage":"Nie znaleziono","details":null}
                ]}""";

        assertThat(client.describeErrors(response(404, body)))
                .isEqualTo("NotFound: Checkout form not found (checkoutForm)");
    }

    @Test
    void shouldNotLeakBodyWhenErrorEnvelopeIsUnparseable() {
        String html = "<html><body>502 Bad Gateway buyer@example.com</body></html>";

        assertThat(client.describeErrors(response(502, html)))
                .isEqualTo("unparseable error body")
                .doesNotContain("buyer@example.com");
    }

    @Test
    void shouldReportMissingDetailsForEmptyEnvelope() {
        assertThat(client.describeErrors(response(400, "{\"errors\":[]}")))
                .isEqualTo("no error details");
    }

    private Response response(int code, String body) {
        return new Response(code, "", Map.of(), body);
    }
}
