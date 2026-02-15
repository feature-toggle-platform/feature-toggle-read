package pl.feature.toggle.service.read.infrastructure.in.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentTypeChanged;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentUpdated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleStatusChanged;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleValueChanged;
import pl.feature.toggle.service.contracts.event.project.ProjectCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged;
import pl.feature.toggle.service.contracts.event.project.ProjectUpdated;
import pl.feature.toggle.service.event.processing.api.EventProcessor;
import pl.feature.toggle.service.read.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjection;
import pl.feature.toggle.service.read.application.port.in.ProjectProjection;

@Slf4j
@AllArgsConstructor
@KafkaListener(topics = {"${topics.feature-toggle-events}", "${topics.feature-toggle-configuration-events}"})
class KafkaEventConsumer {

    private final FeatureToggleProjection featureToggleProjection;
    private final ProjectProjection projectProjection;
    private final EnvironmentProjection environmentProjection;
    private final EventProcessor eventProcessor;

    @KafkaHandler
    void handle(FeatureToggleCreated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                featureToggleProjection::handle,
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(FeatureToggleUpdated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                featureToggleProjection::handle,
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(FeatureToggleStatusChanged event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                featureToggleProjection::handle,
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(FeatureToggleValueChanged event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                featureToggleProjection::handle,
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(ProjectCreated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                projectProjection::handle,
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(ProjectUpdated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                projectProjection::handle,
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(ProjectStatusChanged event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                projectProjection::handle,
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(EnvironmentCreated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                environmentProjection::handle,
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(EnvironmentUpdated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                environmentProjection::handle,
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(EnvironmentStatusChanged event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                environmentProjection::handle,
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(EnvironmentTypeChanged event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                environmentProjection::handle,
                acknowledgment::acknowledge);
    }


}
