package com.configly.read.infrastructure.in.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import com.configly.contracts.event.environment.EnvironmentCreated;
import com.configly.contracts.event.environment.EnvironmentStatusChanged;
import com.configly.contracts.event.environment.EnvironmentTypeChanged;
import com.configly.contracts.event.environment.EnvironmentUpdated;
import com.configly.contracts.event.featuretoggle.FeatureToggleCreated;
import com.configly.contracts.event.featuretoggle.FeatureToggleStatusChanged;
import com.configly.contracts.event.featuretoggle.FeatureToggleUpdated;
import com.configly.contracts.event.featuretoggle.FeatureToggleValueChanged;
import com.configly.contracts.event.project.ProjectCreated;
import com.configly.contracts.event.project.ProjectStatusChanged;
import com.configly.contracts.event.project.ProjectUpdated;
import com.configly.event.processing.api.EventProcessor;
import com.configly.read.application.handler.FeatureToggleSseNotifier;
import com.configly.read.application.port.in.EnvironmentProjection;
import com.configly.read.application.port.in.FeatureToggleProjection;
import com.configly.read.application.port.in.ProjectProjection;

@Slf4j
@AllArgsConstructor
@KafkaListener(topics = {"${topics.feature-toggle-events}", "${topics.feature-toggle-configuration-events}"})
class KafkaEventConsumer {

    private final FeatureToggleProjection featureToggleProjection;
    private final ProjectProjection projectProjection;
    private final EnvironmentProjection environmentProjection;
    private final EventProcessor eventProcessor;
    private final FeatureToggleSseNotifier featureToggleSseNotifier;

    @KafkaHandler
    void handle(FeatureToggleCreated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                featureToggleProjection::handle,
                () -> featureToggleSseNotifier.rebuildRequiredForEnvironment(event.environmentId(), event.revision()),
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(FeatureToggleUpdated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                featureToggleProjection::handle,
                () -> featureToggleSseNotifier.rebuildRequiredForEnvironment(event.environmentId(), event.revision()),
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(FeatureToggleStatusChanged event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                featureToggleProjection::handle,
                () -> featureToggleSseNotifier.rebuildRequiredForEnvironment(event.environmentId(), event.revision()),
                acknowledgment::acknowledge);
    }

    @KafkaHandler
    void handle(FeatureToggleValueChanged event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                featureToggleProjection::handle,
                () -> featureToggleSseNotifier.rebuildRequiredForEnvironment(event.environmentId(), event.revision()),
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
                () -> featureToggleSseNotifier.rebuildRequiredForProject(event.projectId(), event.revision()),
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
                () -> featureToggleSseNotifier.rebuildRequiredForEnvironment(event.environmentId(), event.revision()),
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
