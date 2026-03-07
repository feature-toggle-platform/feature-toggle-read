package pl.feature.toggle.service.read.application.handler;

import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.in.SseConnection;
import pl.feature.toggle.service.read.application.port.out.sse.SseClients;
import pl.feature.toggle.service.read.application.port.out.sse.SseEvent;
import pl.feature.toggle.service.read.application.port.out.sse.SseScope;
import pl.feature.toggle.service.read.application.port.out.sse.SseSubscription;
import pl.feature.toggle.service.read.infrastructure.support.TestSseConnection;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureToggleSseHandlerTest {

    private final TestSseClients sseClients = new TestSseClients();
    private final FeatureToggleSseHandler handler = new FeatureToggleSseHandler(sseClients);

    @Test
    void should_register_connection_for_environment_scope() {
        // given
        var projectId = ProjectId.create(UUID.randomUUID().toString());
        var environmentId = EnvironmentId.create(UUID.randomUUID().toString());
        var connection = new TestSseConnection();

        // when
        handler.establish(projectId, environmentId, connection);

        // then
        assertThat(sseClients.registeredScope())
                .isEqualTo(SseScope.environmentScope(
                        projectId.uuid(),
                        environmentId.uuid()
                ));
        assertThat(sseClients.registeredConnection()).isSameAs(connection);
    }

    @Test
    void should_send_connected_event_after_establishing_connection() {
        // given
        var projectId = ProjectId.create(UUID.randomUUID().toString());
        var environmentId = EnvironmentId.create(UUID.randomUUID().toString());
        var connection = new TestSseConnection();

        // when
        handler.establish(projectId, environmentId, connection);

        // then
        assertThat(connection.sentEvents())
                .containsExactly(SseEvent.connectedEvent());
    }

    @Test
    void should_unsubscribe_when_connection_is_closed() {
        // given
        var projectId = ProjectId.create(UUID.randomUUID().toString());
        var environmentId = EnvironmentId.create(UUID.randomUUID().toString());
        var connection = new TestSseConnection();

        // when
        handler.establish(projectId, environmentId, connection);
        connection.close();

        // then
        assertThat(sseClients.unsubscribeCalls()).isEqualTo(1);
    }

    @Test
    void should_unsubscribe_when_connection_reports_error() {
        // given
        var projectId = ProjectId.create(UUID.randomUUID().toString());
        var environmentId = EnvironmentId.create(UUID.randomUUID().toString());
        var connection = new TestSseConnection();

        // when
        handler.establish(projectId, environmentId, connection);
        connection.fail(new RuntimeException("boom"));

        // then
        assertThat(sseClients.unsubscribeCalls()).isEqualTo(1);
    }

    private static final class TestSseClients implements SseClients {

        private SseScope registeredScope;
        private SseConnection registeredConnection;
        private int unsubscribeCalls;

        @Override
        public SseSubscription register(SseScope scope, SseConnection sseConnection) {
            this.registeredScope = scope;
            this.registeredConnection = sseConnection;
            return () -> unsubscribeCalls++;
        }

        @Override
        public void broadcast(SseScope broadcastScope, SseEvent event) {
        }

        SseScope registeredScope() {
            return registeredScope;
        }

        SseConnection registeredConnection() {
            return registeredConnection;
        }

        int unsubscribeCalls() {
            return unsubscribeCalls;
        }
    }

}