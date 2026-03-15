package pl.feature.toggle.service.read.infrastructure.in.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.event.processing.api.EventProcessor;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.AbstractITTest;
import pl.feature.toggle.service.read.FakeAcknowledgment;
import pl.feature.toggle.service.read.application.handler.FeatureToggleSseNotifier;
import pl.feature.toggle.service.read.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjection;
import pl.feature.toggle.service.read.application.port.in.ProjectProjection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated.environmentCreatedEventBuilder;

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