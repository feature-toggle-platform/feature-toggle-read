package pl.feature.toggle.service.read.infrastructure.out.sse;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pl.feature.toggle.service.read.application.port.in.SseConnection;
import pl.feature.toggle.service.read.application.port.out.sse.SseClients;
import pl.feature.toggle.service.read.application.port.out.sse.SseEvent;
import pl.feature.toggle.service.read.application.port.out.sse.SseScope;
import pl.feature.toggle.service.read.application.port.out.sse.SseSubscription;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Log4j2
final class InMemorySseClients implements SseClients {

    private final Map<SseScope, Set<Client>> clientsByScope = new ConcurrentHashMap<>();

    static SseClients create() {
        return new InMemorySseClients();
    }

    @Override
    public SseSubscription register(SseScope scope, SseConnection sseConnection) {
        var client = new Client(UUID.randomUUID(), sseConnection);

        clientsByScope
                .computeIfAbsent(scope, __ -> ConcurrentHashMap.newKeySet())
                .add(client);

        return () -> unregister(scope, client);
    }

    @Override
    public void broadcast(SseScope broadcastScope, SseEvent event) {
        for (var targetScope : matchingScopes(broadcastScope)) {
            sendToScope(targetScope, event);
        }
    }

    private Iterable<SseScope> matchingScopes(SseScope broadcastScope) {
        if (broadcastScope.isAllScope()) {
            return clientsByScope.keySet();
        }

        return clientsByScope.keySet().stream()
                .filter(clientScope -> matches(broadcastScope, clientScope))
                .toList();
    }

    private void sendToScope(SseScope scope, SseEvent event) {
        var clients = clientsByScope.get(scope);
        if (clients == null || clients.isEmpty()) {
            return;
        }

        for (var client : clients) {
            try {
                client.connection.send(event);
            } catch (Exception ex) {
                unregister(scope, client);
                log.warn("[SSE] send failed: {}", ex.getMessage(), ex);
            }
        }
    }

    private boolean matches(SseScope broadcastScope, SseScope clientScope) {
        if (broadcastScope.isAllScope()) {
            return true;
        }
        if (broadcastScope.isProjectScope()) {
            return broadcastScope.projectId().equals(clientScope.projectId());
        }
        return broadcastScope.equals(clientScope);
    }

    private void unregister(SseScope scope, Client client) {
        var clients = clientsByScope.get(scope);
        if (clients == null) {
            return;
        }
        clients.remove(client);
        if (clients.isEmpty()) {
            clientsByScope.remove(scope, clients);
        }
    }

    private record Client(
            UUID id,
            SseConnection connection
    ) {

    }
}
