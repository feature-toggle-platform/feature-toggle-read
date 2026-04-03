package com.configly.read.application.projection.environment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.configly.model.Revision;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.environment.EnvironmentStatus;
import com.configly.model.project.ProjectId;
import com.configly.read.AbstractUnitTest;
import com.configly.read.application.port.in.EnvironmentProjection;
import com.configly.read.application.projection.environment.event.EnvironmentViewRebuildRequested;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static com.configly.contracts.fake.event.FakeEnvironmentCreatedBuilder.fakeEnvironmentCreatedBuilder;
import static com.configly.contracts.fake.event.FakeEnvironmentStatusChangedBuilder.fakeEnvironmentStatusChangedBuilder;
import static com.configly.contracts.fake.event.FakeEnvironmentTypeChangedBuilder.fakeEnvironmentTypeChangedBuilder;
import static com.configly.contracts.fake.event.FakeEnvironmentUpdatedBuilder.fakeEnvironmentUpdatedBuilder;
import static com.configly.read.builder.FakeEnvironmentViewBuilder.fakeEnvironmentViewBuilder;

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

        var event = fakeEnvironmentCreatedBuilder()
                .withProjectId(projectId.uuid())
                .withEnvironmentId(envId.uuid())
                .withCreatedAt(LocalDateTime.now())
                .withEnvironmentName("test")
                .withStatus(EnvironmentStatus.ACTIVE.name())
                .withRevision(Revision.initialRevision().value())
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

        var event = fakeEnvironmentStatusChangedBuilder()
                .withProjectId(existingEnv.projectId().uuid())
                .withEnvironmentId(existingEnv.id().uuid())
                .withStatus(EnvironmentStatus.ARCHIVED.name())
                .withRevision(existingEnv.revision().next().value())
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

        var event = fakeEnvironmentTypeChangedBuilder()
                .withProjectId(existingEnv.projectId().uuid())
                .withEnvironmentId(existingEnv.id().uuid())
                .withType("DEV")
                .withRevision(existingEnv.revision().next().value())
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

        var event = fakeEnvironmentUpdatedBuilder()
                .withProjectId(existingEnv.projectId().uuid())
                .withEnvironmentId(existingEnv.id().uuid())
                .withEnvironmentName("AFTER")
                .withRevision(existingEnv.revision().next().value())
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

        var event = fakeEnvironmentStatusChangedBuilder()
                .withProjectId(existing.projectId().uuid())
                .withEnvironmentId(existing.id().uuid())
                .withStatus(EnvironmentStatus.ARCHIVED.name())
                .withRevision(5)
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
