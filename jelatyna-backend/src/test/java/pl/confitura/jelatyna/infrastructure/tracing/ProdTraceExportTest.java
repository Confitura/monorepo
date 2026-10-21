package pl.confitura.jelatyna.infrastructure.tracing;

import com.sun.net.httpserver.HttpServer;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.micrometer.tracing.opentelemetry.autoconfigure.otlp.OtlpTracingAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

// Prod's configuration files with Coolify's environment variables. Coolify already sets the
// endpoint of an Alloy collector that isn't deployed; only the opt-in turns export on.
class ProdTraceExportTest {

    private static final String ENDPOINT = "MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_ENDPOINT";

    private final ApplicationContextRunner prod = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withPropertyValues("spring.profiles.active=prod")
            .withConfiguration(AutoConfigurations.of(OtlpTracingAutoConfiguration.class));

    @Test
    void theCoolifyEndpointAloneExportsNothing() {
        prod.withInitializer(environmentVariables(Map.of(ENDPOINT, "http://alloy:4318/v1/traces")))
                .run(context -> assertThat(context).doesNotHaveBean(SpanExporter.class));
    }

    @Test
    void optingInExportsTracesOverHttp() throws IOException {
        List<ExportTraceServiceRequest> received = new CopyOnWriteArrayList<>();
        HttpServer collector = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        collector.createContext("/v1/traces", exchange -> {
            received.add(ExportTraceServiceRequest.parseFrom(exchange.getRequestBody()));
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        collector.start();
        try {
            prod.withInitializer(environmentVariables(Map.of(
                            ENDPOINT, "http://localhost:" + collector.getAddress().getPort() + "/v1/traces",
                            "MANAGEMENT_TRACING_EXPORT_OTLP_ENABLED", "true")))
                    .run(context -> {
                        SpanExporter exporter = context.getBean(SpanExporter.class);
                        assertThat(exporter).isInstanceOf(OtlpHttpSpanExporter.class);

                        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                                .addSpanProcessor(SimpleSpanProcessor.create(exporter))
                                .build();
                        tracerProvider.get("prod-check").spanBuilder("prod-check").startSpan().end();
                        tracerProvider.forceFlush().join(10, TimeUnit.SECONDS);

                        assertThat(received).hasSize(1);
                    });
        } finally {
            collector.stop(0);
        }
    }

    private static ApplicationContextInitializer<ConfigurableApplicationContext> environmentVariables(Map<String, Object> variables) {
        // Named like the real one so Boot maps the variable names to property names.
        return context -> context.getEnvironment().getPropertySources()
                .addFirst(new SystemEnvironmentPropertySource("test-systemEnvironment", variables));
    }
}
