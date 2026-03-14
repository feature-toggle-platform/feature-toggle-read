package pl.feature.toggle.service.read.application.handler;

import lombok.AllArgsConstructor;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleSseUseCase;
import pl.feature.toggle.service.read.application.port.in.SseConnection;
import pl.feature.toggle.service.read.application.port.out.sse.SseClients;
import pl.feature.toggle.service.read.application.port.out.sse.SseEvent;
import pl.feature.toggle.service.read.application.port.out.sse.SseScope;

@AllArgsConstructor
class FeatureToggleSseHandler implements FeatureToggleSseUseCase {

    private final SseClients sseClients;

    @Override
    public void establish(ProjectId projectId, EnvironmentId environmentId, SseConnection sseConnection) {
        var scope = SseScope.resolveScope(projectId, environmentId);
        var subscription = sseClients.register(scope, sseConnection);
        sseConnection.onClose(subscription::unsubscribe);
        sseConnection.onError(ex -> subscription.unsubscribe());
        sseConnection.send(SseEvent.connectedEvent());
    }
}
