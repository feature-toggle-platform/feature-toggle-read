package pl.feature.toggle.service.read.application.projection.featuretoggle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.read.AbstractUnitTest;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleProjection;
import pl.feature.toggle.service.read.application.projection.featuretoggle.event.FeatureToggleViewRebuildRequested;
import pl.feature.toggle.service.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.value.FeatureToggleValueType;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated.featureToggleCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleStatusChanged.featureToggleStatusChangedBuilder;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated.featureToggleUpdatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleValueChanged.featureToggleValueChangedBuilder;
import static pl.feature.toggle.service.read.builder.FakeFeatureToggleViewBuilder.fakeFeatureToggleViewBuilder;

class FeatureToggleProjectionHandlerTest extends AbstractUnitTest {

    private FeatureToggleProjection sut;

    @BeforeEach
    void setUp() {
        sut = FeatureToggleProjectionFacade.featureToggleProjection(
                featureToggleViewProjectionRepositorySpy,
                featureToggleViewQueryRepositoryStub,
                revisionProjectionApplier,
                applicationEventPublishedSpy
        );
    }

    @Test
    void should_insert_new_feature_toggle_when_feature_toggle_not_exists() {
        // given
        featureToggleViewQueryRepositoryStub.findReturns(null);
        featureToggleViewProjectionRepositorySpy.expectNoUpdates();
        featureToggleViewProjectionRepositorySpy.expectNoUpserts();
        featureToggleViewProjectionRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var featureToggleId = FeatureToggleId.create();

        var event = featureToggleCreatedEventBuilder()
                .projectId(ProjectId.create().uuid())
                .id(featureToggleId.uuid())
                .name("TEST")
                .environmentId(EnvironmentId.create().uuid())
                .description("TEST_DESCRIPTION")
                .value("TRUE")
                .type("BOOLEAN")
                .createdAt(LocalDateTime.now())
                .status(FeatureToggleStatus.ACTIVE.name())
                .revision(Revision.initialRevision().value())
                .build();

        // when
        sut.handle(event);

        // then
        var actual = featureToggleViewProjectionRepositorySpy.lastInserted();
        assertThat(actual.id()).isEqualTo(featureToggleId);
        assertThat(actual.status()).isEqualTo(FeatureToggleStatus.ACTIVE);
        assertThat(actual.revision()).isEqualTo(Revision.initialRevision());
        assertThat(actual.consistent()).isTrue();
    }

    @Test
    void should_update_feature_toggle_status() {
        var existing = fakeFeatureToggleViewBuilder()
                .status(FeatureToggleStatus.ACTIVE)
                .build();

        featureToggleViewQueryRepositoryStub.findReturns(existing);
        featureToggleViewProjectionRepositorySpy.expectNoInserts();
        featureToggleViewProjectionRepositorySpy.expectNoUpserts();
        featureToggleViewProjectionRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var event = featureToggleStatusChangedBuilder()
                .projectId(existing.id().uuid())
                .status(ProjectStatus.ARCHIVED.name())
                .revision(existing.revision().next().value())
                .build();

        // when
        sut.handle(event);

        // then
        var updated = featureToggleViewProjectionRepositorySpy.lastUpdated();
        assertThat(updated.status()).isEqualTo(FeatureToggleStatus.ARCHIVED);
        assertThat(updated.revision()).isEqualTo(existing.revision().next());
    }

    @Test
    void should_update_feature_toggle_value() {
        var existing = fakeFeatureToggleViewBuilder()
                .status(FeatureToggleStatus.ACTIVE)
                .value(FeatureToggleValueBuilder.bool(true))
                .build();

        featureToggleViewQueryRepositoryStub.findReturns(existing);
        featureToggleViewProjectionRepositorySpy.expectNoInserts();
        featureToggleViewProjectionRepositorySpy.expectNoUpserts();
        featureToggleViewProjectionRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var event = featureToggleValueChangedBuilder()
                .projectId(existing.id().uuid())
                .type(FeatureToggleValueType.BOOLEAN.name())
                .value(FeatureToggleValueBuilder.bool(false).asText())
                .revision(existing.revision().next().value())
                .build();

        // when
        sut.handle(event);

        // then
        var updated = featureToggleViewProjectionRepositorySpy.lastUpdated();
        assertThat(updated.value().asText()).isEqualTo(event.value());
        assertThat(updated.revision()).isEqualTo(existing.revision().next());
    }

    @Test
    void should_update_feature_toggle_basic_fields() {
        var existing = fakeFeatureToggleViewBuilder()
                .status(FeatureToggleStatus.ACTIVE)
                .name("old name")
                .description("old description")
                .build();

        featureToggleViewQueryRepositoryStub.findReturns(existing);
        featureToggleViewProjectionRepositorySpy.expectNoInserts();
        featureToggleViewProjectionRepositorySpy.expectNoUpserts();
        featureToggleViewProjectionRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var event = featureToggleUpdatedEventBuilder()
                .projectId(existing.id().uuid())
                .name("new name")
                .description("new description")
                .revision(existing.revision().next().value())
                .build();

        // when
        sut.handle(event);

        // then
        var updated = featureToggleViewProjectionRepositorySpy.lastUpdated();
        assertThat(updated.name().value()).isEqualTo(event.name());
        assertThat(updated.description().value()).isEqualTo(event.description());
        assertThat(updated.revision()).isEqualTo(existing.revision().next());
    }

    @Test
    void should_publish_rebuild_requested_event_with_correct_id_when_gap_detected() {
        var existing = fakeFeatureToggleViewBuilder()
                .revision(Revision.from(2))
                .build();

        featureToggleViewQueryRepositoryStub.findReturns(existing);
        featureToggleViewProjectionRepositorySpy.expectNoInserts();
        featureToggleViewProjectionRepositorySpy.expectNoUpserts();
        featureToggleViewProjectionRepositorySpy.expectNoUpdates();
        featureToggleViewProjectionRepositorySpy.markInconsistentIfNotMarkedReturns(true);

        var event = featureToggleStatusChangedBuilder()
                .projectId(existing.projectId().uuid())
                .id(existing.id().uuid())
                .status(ProjectStatus.ARCHIVED.name())
                .revision(5)
                .build();

        // when
        sut.handle(event);

        // then
        var internalEvent = applicationEventPublishedSpy.getLastEvent(FeatureToggleViewRebuildRequested.class);
        assertThat(internalEvent).isNotNull();
        assertThat(internalEvent.featureToggleId()).isEqualTo(existing.id());
    }
}