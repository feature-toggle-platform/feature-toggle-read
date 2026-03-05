package pl.feature.toggle.service.read.application.port.out.sse;

import pl.feature.toggle.service.read.application.port.in.SseConnection;

public interface SseClients {

    SseSubscription register(SseScope scope, SseConnection sseConnection);

    void broadcast(SseScope scope, SseEvent event);

}
