package pl.confitura.jelatyna.platform.tracing;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import io.grpc.MethodDescriptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.ServerCalls;
import io.micrometer.registry.otlp.OtlpMeterRegistry;
import io.micrometer.tracing.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import pl.confitura.jelatyna.BaseIntegrationTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

// IDEA's OpenTelemetry plugin gives a run OTEL_EXPORTER_OTLP_* variables pointing at its own
// receiver, which accepts OTLP over gRPC only. Boot maps those variables to the properties set
// below; the receiver here stands in for the plugin's.
class IdeaOpenTelemetryPluginTest extends BaseIntegrationTest {

    private static final String TRACE_SERVICE = "opentelemetry.proto.collector.trace.v1.TraceService";

    private static final List<ExportTraceServiceRequest> received = new CopyOnWriteArrayList<>();

    private static final Server receiver = startReceiver();

    @DynamicPropertySource
    static void pluginSettings(DynamicPropertyRegistry registry) {
        String endpoint = "http://localhost:" + receiver.getPort();
        // OTEL_EXPORTER_OTLP_{TRACES,LOGS}_ENDPOINT and OTEL_EXPORTER_OTLP_{TRACES,LOGS}_PROTOCOL=grpc
        registry.add("management.opentelemetry.tracing.export.otlp.endpoint", () -> endpoint);
        registry.add("management.opentelemetry.tracing.export.otlp.transport", () -> "grpc");
        registry.add("management.opentelemetry.logging.export.otlp.endpoint", () -> endpoint);
        registry.add("management.opentelemetry.logging.export.otlp.transport", () -> "grpc");
        // OTEL_EXPORTER_OTLP_METRICS_ENDPOINT and OTEL_METRIC_EXPORT_INTERVAL=1000
        registry.add("management.otlp.metrics.export.url", () -> endpoint);
        registry.add("management.otlp.metrics.export.step", () -> "1s");
    }

    @Autowired
    ApplicationContext context;

    @Autowired
    Tracer tracer;

    @Autowired
    SdkTracerProvider tracerProvider;

    @AfterAll
    static void stopReceiver() {
        receiver.shutdownNow();
    }

    @Test
    void withThePluginsSettingsSpansReachItsGrpcReceiver() {
        assertThat(context.getBean(SpanExporter.class)).isInstanceOf(OtlpGrpcSpanExporter.class);

        tracer.nextSpan().name("idea-plugin-check").start().end();
        tracerProvider.forceFlush().join(10, TimeUnit.SECONDS);

        assertThat(received).flatExtracting(IdeaOpenTelemetryPluginTest::spanNames).contains("idea-plugin-check");
    }

    // Micrometer sends OTLP metrics over HTTP only, which the plugin's receiver would reject every second.
    @Test
    void withThePluginsSettingsNoMetricsAreSentOverHttp() {
        assertThat(context.getBeanNamesForType(OtlpMeterRegistry.class)).isEmpty();
    }

    private static List<String> spanNames(ExportTraceServiceRequest request) {
        return request.getResourceSpansList().stream()
                .flatMap(resourceSpans -> resourceSpans.getScopeSpansList().stream())
                .flatMap(scopeSpans -> scopeSpans.getSpansList().stream())
                .map(Span::getName)
                .toList();
    }

    private static Server startReceiver() {
        MethodDescriptor<ExportTraceServiceRequest, ExportTraceServiceResponse> export = MethodDescriptor
                .newBuilder(protobuf(ExportTraceServiceRequest.parser()), protobuf(ExportTraceServiceResponse.parser()))
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName(MethodDescriptor.generateFullMethodName(TRACE_SERVICE, "Export"))
                .build();
        ServerServiceDefinition traceService = ServerServiceDefinition.builder(TRACE_SERVICE)
                .addMethod(export, ServerCalls.asyncUnaryCall((request, response) -> {
                    received.add(request);
                    response.onNext(ExportTraceServiceResponse.getDefaultInstance());
                    response.onCompleted();
                }))
                .build();
        try {
            return ServerBuilder.forPort(0).addService(traceService).build().start();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static <T extends MessageLite> MethodDescriptor.Marshaller<T> protobuf(Parser<T> parser) {
        return new MethodDescriptor.Marshaller<>() {
            @Override
            public InputStream stream(T message) {
                return message.toByteString().newInput();
            }

            @Override
            public T parse(InputStream stream) {
                try {
                    return parser.parseFrom(stream);
                } catch (InvalidProtocolBufferException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        };
    }
}
