package com.configly.read.infrastructure.out.sse;

import org.junit.jupiter.api.Test;
import com.configly.read.application.port.out.sse.SseClients;
import com.configly.read.application.port.out.sse.SseEvent;
import com.configly.read.application.port.out.sse.SseScope;
import com.configly.read.infrastructure.support.TestSseConnection;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InMemorySseClientsTest {

    private final SseClients sseClients = InMemorySseClients.create();

    @Test
    void should_send_event_to_client_registered_for_exact_environment_scope() {
        // given
        var environmentId = UUID.randomUUID();
        var scope = SseScope.environmentScope(environmentId);
        var connection = new TestSseConnection();

        sseClients.register(scope, connection);
        var event = SseEvent.rebuildRequired(10L);

        // when
        sseClients.broadcast(scope, event);

        // then
        assertThat(connection.sentEvents()).containsExactly(event);
    }

    @Test
    void should_not_send_event_to_client_registered_for_other_environment_scope() {
        // given
        var registeredEnvironmentId = UUID.randomUUID();
        var otherEnvironmentId = UUID.randomUUID();

        var registeredScope = SseScope.environmentScope(registeredEnvironmentId);
        var broadcastScope = SseScope.environmentScope(otherEnvironmentId);
        var connection = new TestSseConnection();

        sseClients.register(registeredScope, connection);
        var event = SseEvent.rebuildRequired(10L);

        // when
        sseClients.broadcast(broadcastScope, event);

        // then
        assertThat(connection.sentEvents()).isEmpty();
    }

    @Test
    void should_send_event_only_to_client_registered_for_exact_project_scope() {
        // given
        var projectId = UUID.randomUUID();
        var otherProjectId = UUID.randomUUID();

        var connection1 = new TestSseConnection();
        var connection2 = new TestSseConnection();
        var connection3 = new TestSseConnection();

        sseClients.register(SseScope.projectScope(projectId), connection1);
        sseClients.register(SseScope.projectScope(projectId), connection2);
        sseClients.register(SseScope.projectScope(otherProjectId), connection3);
        var event = SseEvent.rebuildRequired(20L);

        // when
        sseClients.broadcast(SseScope.projectScope(projectId), event);

        // then
        assertThat(connection1.sentEvents()).containsExactly(event);
        assertThat(connection2.sentEvents()).containsExactly(event);
        assertThat(connection3.sentEvents()).isEmpty();
    }

    @Test
    void should_not_send_event_to_environment_scope_when_broadcasting_project_scope() {
        // given
        var projectId = UUID.randomUUID();
        var environmentId = UUID.randomUUID();

        var connection = new TestSseConnection();
        sseClients.register(SseScope.environmentScope(environmentId), connection);

        var event = SseEvent.rebuildRequired(25L);

        // when
        sseClients.broadcast(SseScope.projectScope(projectId), event);

        // then
        assertThat(connection.sentEvents()).isEmpty();
    }

    @Test
    void should_send_event_to_all_registered_clients_for_all_scope() {
        // given
        var connection1 = new TestSseConnection();
        var connection2 = new TestSseConnection();
        var connection3 = new TestSseConnection();

        sseClients.register(SseScope.environmentScope(UUID.randomUUID()), connection1);
        sseClients.register(SseScope.projectScope(UUID.randomUUID()), connection2);
        sseClients.register(SseScope.environmentScope(UUID.randomUUID()), connection3);
        var event = SseEvent.rebuildRequired(30L);

        // when
        sseClients.broadcast(SseScope.allScope(), event);

        // then
        assertThat(connection1.sentEvents()).containsExactly(event);
        assertThat(connection2.sentEvents()).containsExactly(event);
        assertThat(connection3.sentEvents()).containsExactly(event);
    }

    @Test
    void should_unregister_client_after_unsubscribe() {
        // given
        var environmentId = UUID.randomUUID();
        var scope = SseScope.environmentScope(environmentId);
        var connection = new TestSseConnection();

        var subscription = sseClients.register(scope, connection);
        subscription.unsubscribe();
        var event = SseEvent.rebuildRequired(40L);

        // when
        sseClients.broadcast(scope, event);

        // then
        assertThat(connection.sentEvents()).isEmpty();
    }

    @Test
    void should_unregister_only_one_client_after_unsubscribe() {
        // given
        var environmentId = UUID.randomUUID();
        var scope = SseScope.environmentScope(environmentId);

        var connection1 = new TestSseConnection();
        var connection2 = new TestSseConnection();

        var subscription1 = sseClients.register(scope, connection1);
        sseClients.register(scope, connection2);

        subscription1.unsubscribe();
        var event = SseEvent.rebuildRequired(50L);

        // when
        sseClients.broadcast(scope, event);

        // then
        assertThat(connection1.sentEvents()).isEmpty();
        assertThat(connection2.sentEvents()).containsExactly(event);
    }

}