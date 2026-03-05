package pl.feature.toggle.service.read.application.port.in;

import pl.feature.toggle.service.read.application.port.out.sse.SseEvent;

import java.util.function.Consumer;

public interface SseConnection {

    void send(SseEvent event);

    void complete();

    void onClose(Runnable callback);

    void onError(Consumer<Throwable> callback);

}
