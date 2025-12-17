package pl.feature.toggle.service.infrastructure.in.kafka;

import com.ftaas.contracts.event.featuretoggle.FeatureToggleCreated;
import com.ftaas.contracts.event.featuretoggle.FeatureToggleDeleted;
import com.ftaas.contracts.event.featuretoggle.FeatureToggleUpdated;
import com.ftaas.contracts.shared.EventProcessor;
import pl.feature.toggle.service.application.port.in.FeatureToggleProjectionUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@AllArgsConstructor
@KafkaListener(topics = "feature-toggle-events")
class KafkaEventConsumer {

    private final FeatureToggleProjectionUseCase projectionUseCase;
    private final EventProcessor eventProcessor;

    @KafkaHandler
    void handle(FeatureToggleCreated event) {
        eventProcessor.process(event, projectionUseCase::handle);
    }

    @KafkaHandler
    void handle(FeatureToggleUpdated event) {
        eventProcessor.process(event, projectionUseCase::handle);
    }

    @KafkaHandler
    void handle(FeatureToggleDeleted event) {
        eventProcessor.process(event, projectionUseCase::handle);
    }

}
