package pl.feature.toggle.service.read.application.handler;

import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.read.application.port.in.SseConnection;
import pl.feature.toggle.service.read.application.port.out.sse.SseClients;
import pl.feature.toggle.service.read.application.port.out.sse.SseEvent;
import pl.feature.toggle.service.read.application.port.out.sse.SseScope;
import pl.feature.toggle.service.read.application.port.out.sse.SseSubscription;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureToggleSseNotifierTest {

    private final TestSseClients sseClients = new TestSseClients();
    private final FeatureToggleSseNotifier notifier = new FeatureToggleSseNotifier(sseClients);

    @Test
    void should_broadcast_rebuild_required_for_environment_scope() {
        // given
        var projectId = UUID.randomUUID();
        var environmentId = UUID.randomUUID();

        // when
        notifier.rebuildRequired(projectId, environmentId, 15L);

        // then
        assertThat(sseClients.broadcastScope())
                .isEqualTo(SseScope.environmentScope(projectId, environmentId));
        assertThat(sseClients.broadcastEvent())
                .isEqualTo(SseEvent.rebuildRequired(15L));
    }

    @Test
    void should_broadcast_rebuild_required_for_project_scope() {
        // given
        var projectId = UUID.randomUUID();

        // when
        notifier.rebuildRequired(projectId, 21L);

        // then
        assertThat(sseClients.broadcastScope())
                .isEqualTo(SseScope.projectScope(projectId));
        assertThat(sseClients.broadcastEvent())
                .isEqualTo(SseEvent.rebuildRequired(21L));
    }

    @Test
    void should_broadcast_rebuild_required_for_all_scope() {
        // given && when
        notifier.rebuildRequired();

        // then
        assertThat(sseClients.broadcastScope())
                .isEqualTo(SseScope.allScope());
        assertThat(sseClients.broadcastEvent())
                .isEqualTo(SseEvent.rebuildRequired(0L));
    }

    private static final class TestSseClients implements SseClients {

        private SseScope broadcastScope;
        private SseEvent broadcastEvent;

        @Override
        public SseSubscription register(SseScope scope, SseConnection sseConnection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void broadcast(SseScope broadcastScope, SseEvent event) {
            this.broadcastScope = broadcastScope;
            this.broadcastEvent = event;
        }

        SseScope broadcastScope() {
            return broadcastScope;
        }

        SseEvent broadcastEvent() {
            return broadcastEvent;
        }
    }
}