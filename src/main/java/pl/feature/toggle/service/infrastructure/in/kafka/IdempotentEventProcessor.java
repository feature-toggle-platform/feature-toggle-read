package pl.feature.toggle.service.infrastructure.in.kafka;

import com.ftaas.contracts.shared.EventProcessor;
import com.ftaas.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.application.port.out.ProcessedEventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@AllArgsConstructor
@Slf4j
class IdempotentEventProcessor implements EventProcessor {

    private final ProcessedEventRepository processedEvents;

    @Override
    public <T extends IntegrationEvent> void process(T event, Consumer<T> action) {
        logReceiveEvent(event);

        if (processedEvents.alreadyProcessed(event.eventId())) {
            logEventAlreadyProcessed(event);
            return;
        }

        action.accept(event);

        processedEvents.markProcessed(event.eventId());
        logProcessedEvent(event);
    }

    private void logReceiveEvent(IntegrationEvent event) {
        log.info("Received integration event: {}", event);
    }

    private void logEventAlreadyProcessed(IntegrationEvent event) {
        log.info("Integration event {} already processed – skipping", event.eventId());
    }

    private void logProcessedEvent(IntegrationEvent event) {
        log.info("Integration event {} processed", event.eventId());
    }
}
