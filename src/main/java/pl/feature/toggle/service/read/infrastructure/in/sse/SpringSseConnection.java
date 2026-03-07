package pl.feature.toggle.service.read.infrastructure.in.sse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.feature.toggle.service.read.application.port.in.SseConnection;
import pl.feature.toggle.service.read.application.port.out.sse.SseEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
class SpringSseConnection implements SseConnection {

    private static final long NO_TIMEOUT = 0L;
    private static final ScheduledExecutorService HEARTBEAT_EXECUTOR = Executors.newScheduledThreadPool(1);

    private final SseEmitter sseEmitter;
    private final ScheduledFuture<?> heartbeatTask;

    static SpringSseConnection create(Long heartbeatIntervalSeconds) {
        var emitter = new SseEmitter(NO_TIMEOUT);
        var heartbeatTask = HEARTBEAT_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().comment("keepalive"));
            } catch (Exception ignored) {
                emitter.complete();
            }
        }, heartbeatIntervalSeconds, heartbeatIntervalSeconds, TimeUnit.SECONDS);

        emitter.onCompletion(() -> heartbeatTask.cancel(true));
        emitter.onTimeout(() -> heartbeatTask.cancel(true));
        emitter.onError(__ -> heartbeatTask.cancel(true));

        return new SpringSseConnection(emitter, heartbeatTask);
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
        heartbeatTask.cancel(true);
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
