package com.configly.read.application.port.out.sse;

import com.configly.read.application.port.in.SseConnection;

public interface SseClients {

    SseSubscription register(SseScope scope, SseConnection sseConnection);

    void broadcast(SseScope scope, SseEvent event);

}
