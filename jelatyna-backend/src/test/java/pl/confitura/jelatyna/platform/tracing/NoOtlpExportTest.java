package pl.confitura.jelatyna.platform.tracing;

import io.micrometer.registry.otlp.OtlpMeterRegistry;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import pl.confitura.jelatyna.BaseIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

// Without IDEA's OpenTelemetry plugin (local runs, tests) nothing is sent over OTLP, so nothing
// can log export errors for want of a receiver.
class NoOtlpExportTest extends BaseIntegrationTest {

    @Autowired
    ApplicationContext context;

    @Test
    void withoutThePluginsSettingsNoOtlpExporterExists() {
        assertThat(context.getBeanNamesForType(SpanExporter.class)).isEmpty();
        assertThat(context.getBeanNamesForType(LogRecordExporter.class)).isEmpty();
        assertThat(context.getBeanNamesForType(OtlpMeterRegistry.class)).isEmpty();
    }
}
