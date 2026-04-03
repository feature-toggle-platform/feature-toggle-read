package com.configly.read.infrastructure.out.sse;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.configly.read.application.port.in.SseConnection;
import com.configly.read.application.port.out.sse.SseClients;
import com.configly.read.application.port.out.sse.SseEvent;
import com.configly.read.application.port.out.sse.SseScope;
import com.configly.read.application.port.out.sse.SseSubscription;

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
    public void broadcast(SseScope scope, SseEvent event) {
        if (scope.isAllScope()) {
            sendToAll(event);
            return;
        }

        sendToScope(scope, event);
    }

    private void sendToAll(SseEvent event) {
        for (var scope : clientsByScope.keySet()) {
            sendToScope(scope, event);
        }
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
