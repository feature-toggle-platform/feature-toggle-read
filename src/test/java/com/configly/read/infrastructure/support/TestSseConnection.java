package com.configly.read.infrastructure.support;

import com.configly.read.application.port.in.SseConnection;
import com.configly.read.application.port.out.sse.SseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class TestSseConnection implements SseConnection {

    private final List<SseEvent> sentEvents = new ArrayList<>();
    private Runnable onClose;
    private Consumer<Throwable> onError;

    @Override
    public void send(SseEvent sseEvent) {
        sentEvents.add(sseEvent);
    }

    @Override
    public void complete() {
    }

    @Override
    public void onClose(Runnable callback) {
        this.onClose = callback;
    }

    @Override
    public void onError(Consumer<Throwable> callback) {
        this.onError = callback;
    }

    public List<SseEvent> sentEvents() {
        return sentEvents;
    }

    public void close() {
        if (onClose != null) {
            onClose.run();
        }
    }

    public void fail(Throwable throwable) {
        if (onError != null) {
            onError.accept(throwable);
        }
    }
}
