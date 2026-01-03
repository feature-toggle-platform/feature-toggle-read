package pl.feature.toggle.service.read.infrastructure.in.kafka;

import org.springframework.kafka.support.Acknowledgment;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleDeleted;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;
import pl.feature.toggle.service.contracts.shared.EventProcessor;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjectionUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@AllArgsConstructor
@KafkaListener(topics = "${topics.feature-toggle-events}")
class KafkaEventConsumer {

    private final FeatureToggleProjectionUseCase projectionUseCase;
    private final EventProcessor eventProcessor;

    @KafkaHandler
    void handle(FeatureToggleCreated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                projectionUseCase::handle,
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(FeatureToggleUpdated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                projectionUseCase::handle,
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(FeatureToggleDeleted event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                projectionUseCase::handle,
                acknowledgment::acknowledge);
    }

}
