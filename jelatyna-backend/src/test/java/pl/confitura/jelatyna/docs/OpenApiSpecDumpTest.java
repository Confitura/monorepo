package pl.confitura.jelatyna.docs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import pl.confitura.jelatyna.BaseIntegrationTest;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Regenerates the committed OpenAPI contract at the repository root by capturing the springdoc
 * document served at {@code /v3/api-docs}. Running the test suite keeps {@code openapi.json} in
 * sync with the controllers; the CI drift guard fails the build when the regenerated file differs
 * from what is committed.
 */
class OpenApiSpecDumpTest extends BaseIntegrationTest {

    private static final Path SPEC_FILE = Path.of("..", "openapi.json");

    // Sort map keys so springdoc's non-deterministic property ordering can't produce spurious
    // drift-guard diffs; the committed file is then a stable, reviewable contract.
    private final JsonMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .build();

    @Test
    void dumpsOpenApiSpec() throws Exception {
        String rawJson = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Map<String, Object> spec = mapper.readValue(rawJson, new tools.jackson.core.type.TypeReference<>() {});
        assertThat(spec).containsKeys("openapi", "paths");

        String prettyJson = mapper.writeValueAsString(spec) + "\n";
        Files.writeString(SPEC_FILE, prettyJson, StandardCharsets.UTF_8);
    }
}
