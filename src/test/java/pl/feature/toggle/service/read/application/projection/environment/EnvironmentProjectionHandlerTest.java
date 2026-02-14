package pl.feature.toggle.service.read.application.projection.environment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.AbstractUnitTest;
import pl.feature.toggle.service.read.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.read.application.projection.environment.event.EnvironmentViewRebuildRequested;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated.environmentCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged.environmentStatusChangedEventBuilder;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentTypeChanged.environmentTypeChangedBuilder;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentUpdated.environmentUpdatedEventBuilder;
import static pl.feature.toggle.service.read.builder.FakeEnvironmentViewBuilder.fakeEnvironmentViewBuilder;

class EnvironmentProjectionHandlerTest extends AbstractUnitTest {

    private EnvironmentProjection sut;

    @BeforeEach
    void setUp() {
        sut = EnvironmentProjectionFacade.environmentProjection(
                environmentViewProjectionRepositorySpy,
                environmentViewQueryRepositoryStub,
                revisionProjectionApplier,
                applicationEventPublishedSpy
        );
    }

    @Test
    void should_insert_new_environment_when_environment_not_exists() {
        // given
        environmentViewQueryRepositoryStub.findReturns(null);
        environmentViewProjectionRepositorySpy.expectNoUpdates();
        environmentViewProjectionRepositorySpy.expectNoUpserts();
        environmentViewProjectionRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var projectId = ProjectId.create();
        var envId = EnvironmentId.create();

        var event = environmentCreatedEventBuilder()
                .projectId(projectId.uuid())
                .environmentId(envId.uuid())
                .createdAt(LocalDateTime.now())
                .environmentName("test")
                .status(EnvironmentStatus.ACTIVE.name())
                .revision(Revision.initialRevision().value())
                .build();

        // when
        sut.handle(event);

        // then
        var actual = environmentViewProjectionRepositorySpy.lastInserted();
        assertThat(actual.projectId()).isEqualTo(projectId);
        assertThat(actual.id()).isEqualTo(envId);
        assertThat(actual.status()).isEqualTo(EnvironmentStatus.ACTIVE);
        assertThat(actual.revision()).isEqualTo(Revision.initialRevision());
        assertThat(actual.consistent()).isTrue();
    }

    @Test
    void should_update_environment_status_when_exists() {
        // given
        var existingEnv = fakeEnvironmentViewBuilder()
                .status(EnvironmentStatus.ACTIVE)
                .build();

        environmentViewQueryRepositoryStub.findReturns(existingEnv);
        environmentViewProjectionRepositorySpy.expectNoInserts();
        environmentViewProjectionRepositorySpy.expectNoUpserts();
        environmentViewProjectionRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var event = environmentStatusChangedEventBuilder()
                .projectId(existingEnv.projectId().uuid())
                .environmentId(existingEnv.id().uuid())
                .status(EnvironmentStatus.ARCHIVED.name())
                .revision(existingEnv.revision().next().value())
                .build();

        // when
        sut.handle(event);

        // then
        var updated = environmentViewProjectionRepositorySpy.lastUpdated();
        assertThat(updated.status()).isEqualTo(EnvironmentStatus.ARCHIVED);
        assertThat(updated.revision()).isEqualTo(existingEnv.revision().next());
    }

    @Test
    void should_update_environment_type() {
        // given
        var existingEnv = fakeEnvironmentViewBuilder()
                .status(EnvironmentStatus.ACTIVE)
                .type("PROD")
                .build();

        environmentViewQueryRepositoryStub.findReturns(existingEnv);
        environmentViewProjectionRepositorySpy.expectNoInserts();
        environmentViewProjectionRepositorySpy.expectNoUpserts();
        environmentViewProjectionRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var event = environmentTypeChangedBuilder()
                .projectId(existingEnv.projectId().uuid())
                .environmentId(existingEnv.id().uuid())
                .type("DEV")
                .revision(existingEnv.revision().next().value())
                .build();

        // when
        sut.handle(event);

        // then
        var updated = environmentViewProjectionRepositorySpy.lastUpdated();
        assertThat(updated.type()).isEqualTo(event.type());
        assertThat(updated.revision()).isEqualTo(existingEnv.revision().next());
    }

    @Test
    void should_update_environment_fields() {
        // given
        var existingEnv = fakeEnvironmentViewBuilder()
                .status(EnvironmentStatus.ACTIVE)
                .name("BEFORE")
                .build();

        environmentViewQueryRepositoryStub.findReturns(existingEnv);
        environmentViewProjectionRepositorySpy.expectNoInserts();
        environmentViewProjectionRepositorySpy.expectNoUpserts();
        environmentViewProjectionRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var event = environmentUpdatedEventBuilder()
                .projectId(existingEnv.projectId().uuid())
                .environmentId(existingEnv.id().uuid())
                .environmentName("AFTER")
                .revision(existingEnv.revision().next().value())
                .build();

        // when
        sut.handle(event);

        // then
        var updated = environmentViewProjectionRepositorySpy.lastUpdated();
        assertThat(updated.name().value()).isEqualTo(event.environmentName());
        assertThat(updated.revision()).isEqualTo(existingEnv.revision().next());
    }

    @Test
    void should_publish_rebuild_requested_event_with_correct_ids_when_gap_detected() {
        var existing = fakeEnvironmentViewBuilder()
                .revision(Revision.from(2))
                .build();

        environmentViewQueryRepositoryStub.findReturns(existing);
        environmentViewProjectionRepositorySpy.expectNoInserts();
        environmentViewProjectionRepositorySpy.expectNoUpserts();
        environmentViewProjectionRepositorySpy.expectNoUpdates();
        environmentViewProjectionRepositorySpy.markInconsistentIfNotMarkedReturns(true);

        var event = environmentStatusChangedEventBuilder()
                .projectId(existing.projectId().uuid())
                .environmentId(existing.id().uuid())
                .status(EnvironmentStatus.ARCHIVED.name())
                .revision(5)
                .build();

        // when
        sut.handle(event);

        // then
        var internalEvent = applicationEventPublishedSpy.getLastEvent(EnvironmentViewRebuildRequested.class);
        assertThat(internalEvent).isNotNull();
        assertThat(internalEvent.projectId()).isEqualTo(existing.projectId());
        assertThat(internalEvent.environmentId()).isEqualTo(existing.id());
    }


}
