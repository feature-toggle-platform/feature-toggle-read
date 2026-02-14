package pl.feature.toggle.service.read.application.projection.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.contracts.event.project.ProjectUpdated;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.read.AbstractUnitTest;
import pl.feature.toggle.service.read.application.port.in.ProjectProjection;
import pl.feature.toggle.service.read.application.projection.project.event.ProjectViewRebuildRequested;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.contracts.event.project.ProjectCreated.projectCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged.projectStatusChangedEventBuilder;
import static pl.feature.toggle.service.contracts.event.project.ProjectUpdated.projectUpdatedEventBuilder;
import static pl.feature.toggle.service.read.builder.FakeProjectViewBuilder.fakeProjectViewBuilder;

class ProjectProjectionHandlerTest extends AbstractUnitTest {

    private ProjectProjection sut;

    @BeforeEach
    void setUp() {
        sut = ProjectProjectionFacade.projectProjection(
                projectViewProjectionRepositorySpy,
                projectViewQueryRepositoryStub,
                revisionProjectionApplier,
                applicationEventPublishedSpy
        );
    }

    @Test
    void should_insert_new_project_when_project_not_exists() {
        // given
        projectViewQueryRepositoryStub.findReturns(null);
        projectViewProjectionRepositorySpy.expectNoUpdates();
        projectViewProjectionRepositorySpy.expectNoUpserts();
        projectViewProjectionRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var projectId = ProjectId.create();

        var event = projectCreatedEventBuilder()
                .projectId(projectId.uuid())
                .projectName("TEST")
                .projectDescription("TEST_DESCRIPTION")
                .createdAt(LocalDateTime.now())
                .status(ProjectStatus.ACTIVE.name())
                .revision(Revision.initialRevision().value())
                .build();

        // when
        sut.handle(event);

        // then
        var actual = projectViewProjectionRepositorySpy.lastInserted();
        assertThat(actual.id()).isEqualTo(projectId);
        assertThat(actual.status()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(actual.revision()).isEqualTo(Revision.initialRevision());
        assertThat(actual.consistent()).isTrue();
    }

    @Test
    void should_change_project_status_when_exists() {
        var existing = fakeProjectViewBuilder()
                .status(ProjectStatus.ACTIVE)
                .build();

        projectViewQueryRepositoryStub.findReturns(existing);
        projectViewProjectionRepositorySpy.expectNoInserts();
        projectViewProjectionRepositorySpy.expectNoUpserts();
        projectViewProjectionRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var event = projectStatusChangedEventBuilder()
                .projectId(existing.id().uuid())
                .status(ProjectStatus.ARCHIVED.name())
                .revision(existing.revision().next().value())
                .build();

        // when
        sut.handle(event);

        // then
        var updated = projectViewProjectionRepositorySpy.lastUpdated();
        assertThat(updated.status()).isEqualTo(ProjectStatus.ARCHIVED);
        assertThat(updated.revision()).isEqualTo(existing.revision().next());
    }

    @Test
    void should_update_project_fields() {
        // given
        var existing = fakeProjectViewBuilder()
                .name("OLD_NAME")
                .status(ProjectStatus.ACTIVE)
                .build();
        projectViewQueryRepositoryStub.findReturns(existing);
        projectViewProjectionRepositorySpy.expectNoInserts();
        projectViewProjectionRepositorySpy.expectNoUpserts();
        projectViewProjectionRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var event = projectUpdatedEventBuilder()
                .projectName("NEW NAME")
                .projectDescription("NEW DESCRIPTION")
                .projectId(existing.id().uuid())
                .revision(existing.revision().next().value())
                .build();

        //when
        sut.handle(event);

        // then
        var updated = projectViewProjectionRepositorySpy.lastUpdated();
        assertThat(updated.name().value()).isEqualTo(event.projectName());
        assertThat(updated.description().value()).isEqualTo(event.projectDescription());
        assertThat(updated.revision().value()).isEqualTo(event.revision());
    }

    @Test
    void should_publish_rebuild_requested_event_with_correct_id_when_gap_detected() {
        var existing = fakeProjectViewBuilder()
                .revision(Revision.from(2))
                .build();

        projectViewQueryRepositoryStub.findReturns(existing);
        projectViewProjectionRepositorySpy.expectNoInserts();
        projectViewProjectionRepositorySpy.expectNoUpserts();
        projectViewProjectionRepositorySpy.expectNoUpdates();
        projectViewProjectionRepositorySpy.markInconsistentIfNotMarkedReturns(true);

        var event = projectStatusChangedEventBuilder()
                .projectId(existing.id().uuid())
                .status(ProjectStatus.ARCHIVED.name())
                .revision(5)
                .build();

        // when
        sut.handle(event);

        // then
        var internalEvent = applicationEventPublishedSpy.getLastEvent(ProjectViewRebuildRequested.class);
        assertThat(internalEvent).isNotNull();
        assertThat(internalEvent.projectId()).isEqualTo(existing.id());
    }
}