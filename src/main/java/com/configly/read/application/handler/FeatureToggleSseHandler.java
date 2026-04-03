package com.configly.read.application.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.read.application.port.in.FeatureToggleSseUseCase;
import com.configly.read.application.port.in.SseConnection;
import com.configly.read.application.port.out.sse.SseClients;
import com.configly.read.application.port.out.sse.SseEvent;
import com.configly.read.application.port.out.sse.SseScope;

@AllArgsConstructor
@Slf4j
class FeatureToggleSseHandler implements FeatureToggleSseUseCase {

    private final SseClients sseClients;

    @Override
    public void establish(ProjectId projectId, EnvironmentId environmentId, SseConnection sseConnection) {
        var scope = SseScope.resolveScope(projectId, environmentId);
        var subscription = sseClients.register(scope, sseConnection);
        sseConnection.onClose(subscription::unsubscribe);
        sseConnection.onError(ex -> subscription.unsubscribe());
        sseConnection.send(SseEvent.connectedEvent());
        log.info("SSE connection established for projectId={}, environmentId={}", projectId, environmentId);
    }
}
