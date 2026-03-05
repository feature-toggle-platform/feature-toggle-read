package pl.feature.toggle.service.read.infrastructure.in.rest.sse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.feature.toggle.service.read.application.port.in.SseConnection;
import pl.feature.toggle.service.read.application.port.out.sse.SseEvent;

import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
class SpringSseConnection implements SseConnection {

    private final SseEmitter sseEmitter;

    static SpringSseConnection create() {
        return new SpringSseConnection(new SseEmitter());
    }

    @Override
    public void send(SseEvent sseEvent) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name(sseEvent.name())
                    .data(sseEvent.data()));
        } catch (Exception e) {
            sseEmitter.completeWithError(e);
        }
    }

    @Override
    public void complete() {
        sseEmitter.complete();
    }

    @Override
    public void onClose(Runnable callback) {
        sseEmitter.onCompletion(callback);
        sseEmitter.onTimeout(callback);
    }

    @Override
    public void onError(Consumer<Throwable> callback) {
        sseEmitter.onError(callback);
    }

    SseEmitter sseEmitter() {
        return sseEmitter;
    }
}
