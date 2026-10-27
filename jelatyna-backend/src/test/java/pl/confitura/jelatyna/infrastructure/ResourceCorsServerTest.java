package pl.confitura.jelatyna.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.resource.ResourceConfigurationProperties;

// A real server, because MockMvc doesn't apply the servlet filter registration that answers CORS.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "app.cors.origins=http://admin.example")
class ResourceCorsServerTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ResourceConfigurationProperties resources;

    private Path file;

    @BeforeEach
    void storeResource() throws IOException {
        file = Files.writeString(Path.of(resources.folder(), UUID.randomUUID() + ".txt"), "hello");
    }

    @AfterEach
    void deleteResource() throws IOException {
        Files.deleteIfExists(file);
    }

    @Test
    void anAllowedOriginReadsAResource() throws Exception {
        for (HttpResponse<Void> response : List.of(fetch("http://admin.example"), preflight("http://admin.example"))) {
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).hasValue("http://admin.example");
            assertThat(response.headers().firstValue("Access-Control-Allow-Credentials")).hasValue("true");
        }
    }

    @Test
    void anyOtherOriginIsRefused() throws Exception {
        for (HttpResponse<Void> response : List.of(fetch("http://elsewhere.example"), preflight("http://elsewhere.example"))) {
            assertThat(response.statusCode()).isEqualTo(403);
            assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).isEmpty();
        }
    }

    private HttpResponse<Void> fetch(String origin) throws Exception {
        return send(request(origin).GET());
    }

    private HttpResponse<Void> preflight(String origin) throws Exception {
        return send(request(origin)
                .header("Access-Control-Request-Method", "GET")
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()));
    }

    private HttpRequest.Builder request(String origin) {
        URI uri = URI.create("http://localhost:" + port + "/api" + resources.path() + "/" + file.getFileName());
        return HttpRequest.newBuilder(uri).header("Origin", origin);
    }

    private HttpResponse<Void> send(HttpRequest.Builder request) throws Exception {
        return HttpClient.newHttpClient().send(request.build(), HttpResponse.BodyHandlers.discarding());
    }
}
