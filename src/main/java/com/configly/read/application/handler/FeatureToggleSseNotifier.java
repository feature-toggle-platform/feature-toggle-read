package com.configly.read.application.handler;

import lombok.AllArgsConstructor;
import com.configly.read.application.port.out.sse.SseClients;
import com.configly.read.application.port.out.sse.SseEvent;
import com.configly.read.application.port.out.sse.SseScope;

import java.util.UUID;

@AllArgsConstructor
public final class FeatureToggleSseNotifier {

    private final SseClients sseClients;

    public void rebuildRequiredForEnvironment(UUID environmentId, long revision) {
        var sseScope = SseScope.environmentScope(environmentId);
        var sseEvent = SseEvent.rebuildRequired(revision);
        sseClients.broadcast(sseScope, sseEvent);
    }

    public void rebuildRequiredForProject(UUID projectId, long revision) {
        var sseScope = SseScope.projectScope(projectId);
        var sseEvent = SseEvent.rebuildRequired(revision);
        sseClients.broadcast(sseScope, sseEvent);
    }

    public void rebuildRequired() {
        var sseScope = SseScope.allScope();
        var sseEvent = SseEvent.rebuildRequired(0L);
        sseClients.broadcast(sseScope, sseEvent);
    }

}
