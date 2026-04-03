package com.configly.read.infrastructure.in.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.configly.event.processing.api.EventProcessor;
import com.configly.model.Revision;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.environment.EnvironmentStatus;
import com.configly.model.project.ProjectId;
import com.configly.read.AbstractITTest;
import com.configly.read.FakeAcknowledgment;
import com.configly.read.application.handler.FeatureToggleSseNotifier;
import com.configly.read.application.port.in.EnvironmentProjection;
import com.configly.read.application.port.in.FeatureToggleProjection;
import com.configly.read.application.port.in.ProjectProjection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static com.configly.contracts.event.environment.EnvironmentCreated.environmentCreatedEventBuilder;

class KafkaEventConsumerIT extends AbstractITTest {

    @Autowired
    private FeatureToggleProjection featureToggleProjection;
    @Autowired
    private ProjectProjection projectProjection;
    @Autowired
    private EnvironmentProjection environmentProjection;
    @Autowired
    private EventProcessor eventProcessor;
    @Autowired
    private FeatureToggleSseNotifier featureToggleSseNotifier;

    private FakeAcknowledgment fakeAcknowledgment;
    private KafkaEventConsumer sut;

    @BeforeEach
    void setUp() {
        fakeAcknowledgment = new FakeAcknowledgment();
        sut = new KafkaEventConsumer(featureToggleProjection, projectProjection, environmentProjection, eventProcessor, featureToggleSseNotifier);
    }

    @Test
    void should_not_mark_event_as_processed_when_projection_handling_fails() {
        // given
        var projectId = ProjectId.create();
        var envId = EnvironmentId.create();

        var event = environmentCreatedEventBuilder()
                .projectId(projectId.uuid())
                .environmentId(envId.uuid())
                .environmentName("test")
                .status(EnvironmentStatus.ACTIVE.name())
                .revision(Revision.initialRevision().value())
                .build();

        // when
        var exception = catchException(() -> sut.handle(event, fakeAcknowledgment));

        // then
        assertThat(exception).isNotNull();
        var processedEvents = getProcessedEvents();
        assertThat(processedEvents).isEmpty();
    }
}