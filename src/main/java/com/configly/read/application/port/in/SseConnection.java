package com.configly.read.application.port.in;

import com.configly.read.application.port.out.sse.SseEvent;

import java.util.function.Consumer;

public interface SseConnection {

    void send(SseEvent event);

    void complete();

    void onClose(Runnable callback);

    void onError(Consumer<Throwable> callback);

}
