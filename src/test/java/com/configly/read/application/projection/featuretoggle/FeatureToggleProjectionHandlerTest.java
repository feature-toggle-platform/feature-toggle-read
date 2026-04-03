package com.configly.read.application.projection.featuretoggle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.configly.model.Revision;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.featuretoggle.FeatureToggleStatus;
import com.configly.model.project.ProjectStatus;
import com.configly.read.AbstractUnitTest;
import com.configly.read.application.port.in.FeatureToggleProjection;
import com.configly.read.application.projection.featuretoggle.event.FeatureToggleViewRebuildRequested;
import com.configly.value.FeatureToggleValueBuilder;
import com.configly.value.FeatureToggleValueType;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static com.configly.contracts.fake.event.FakeFeatureToggleCreatedBuilder.fakeFeatureToggleCreatedBuilder;
import static com.configly.contracts.fake.event.FakeFeatureToggleStatusChangedBuilder.fakeFeatureToggleStatusChangedBuilder;
import static com.configly.contracts.fake.event.FakeFeatureToggleUpdatedBuilder.fakeFeatureToggleUpdatedBuilder;
import static com.configly.contracts.fake.event.FakeFeatureToggleValueChangedBuilder.fakeFeatureToggleValueChangedBuilder;
import static com.configly.read.builder.FakeFeatureToggleViewBuilder.fakeFeatureToggleViewBuilder;

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

        var event = fakeFeatureToggleCreatedBuilder()
                .withId(featureToggleId.uuid())
                .withName("TEST")
                .withEnvironmentId(EnvironmentId.create().uuid())
                .withDescription("TEST_DESCRIPTION")
                .withValue("TRUE")
                .withType("BOOLEAN")
                .withCreatedAt(LocalDateTime.now())
                .withStatus(FeatureToggleStatus.ACTIVE.name())
                .withRevision(Revision.initialRevision().value())
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

        var event = fakeFeatureToggleStatusChangedBuilder()
                .withStatus(ProjectStatus.ARCHIVED.name())
                .withRevision(existing.revision().next().value())
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

        var event = fakeFeatureToggleValueChangedBuilder()
                .withType(FeatureToggleValueType.BOOLEAN.name())
                .withValue(FeatureToggleValueBuilder.bool(false).asText())
                .withRevision(existing.revision().next().value())
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

        var event = fakeFeatureToggleUpdatedBuilder()
                .withName("new name")
                .withDescription("new description")
                .withRevision(existing.revision().next().value())
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

        var event = fakeFeatureToggleStatusChangedBuilder()
                .withId(existing.id().uuid())
                .withStatus(ProjectStatus.ARCHIVED.name())
                .withRevision(5)
                .build();

        // when
        sut.handle(event);

        // then
        var internalEvent = applicationEventPublishedSpy.getLastEvent(FeatureToggleViewRebuildRequested.class);
        assertThat(internalEvent).isNotNull();
        assertThat(internalEvent.featureToggleId()).isEqualTo(existing.id());
    }
}