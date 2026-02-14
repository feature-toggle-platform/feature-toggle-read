package pl.feature.toggle.service.read.infrastructure.out.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.read.AbstractITTest;
import pl.feature.toggle.service.read.application.port.out.EnvironmentProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;
import pl.feature.toggle.service.value.FeatureToggleValueBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.read.builder.FakeEnvironmentViewBuilder.fakeEnvironmentViewBuilder;
import static pl.feature.toggle.service.read.builder.FakeFeatureToggleViewBuilder.fakeFeatureToggleViewBuilder;
import static pl.feature.toggle.service.read.builder.FakeProjectViewBuilder.fakeProjectViewBuilder;

class FeatureToggleProjectionJooqRepositoryIT extends AbstractITTest {

    @Autowired
    private FeatureToggleProjectionRepository sut;

    @Autowired
    private FeatureToggleQueryRepository queryRepository;

    @Autowired
    private ProjectProjectionRepository projectProjectionRepository;

    @Autowired
    private EnvironmentProjectionRepository environmentProjectionRepository;

    @Test
    void should_insert_feature_toggle_view() {
        // given
        insertProject();
        insertEnvironment();

        var toggleId = FeatureToggleId.create();
        var expected = fakeFeatureToggleViewBuilder()
                .id(toggleId)
                .projectId(PROJECT_ID)
                .environmentId(ENVIRONMENT_ID)
                .name("toggle-" + UUID.randomUUID())
                .description("desc-" + UUID.randomUUID())
                .status(FeatureToggleStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .value(FeatureToggleValueBuilder.bool(true))
                .build();

        // when
        sut.insert(expected);

        // then
        var actual = queryRepository.find(toggleId).orElseThrow();

        assertThat(actual.id()).isEqualTo(toggleId);
        assertThat(actual.projectId()).isEqualTo(PROJECT_ID);
        assertThat(actual.environmentId()).isEqualTo(ENVIRONMENT_ID);

        assertThat(actual.name()).isEqualTo(expected.name());
        assertThat(actual.description()).isEqualTo(expected.description());
        assertThat(actual.status()).isEqualTo(expected.status());
        assertThat(actual.revision()).isEqualTo(expected.revision());
        assertThat(actual.consistent()).isTrue();

        assertThat(actual.value()).isEqualTo(expected.value());
    }

    @Test
    void should_update_status_only() {
        // given
        insertProject();
        insertEnvironment();

        var toggleId = FeatureToggleId.create();
        var existing = fakeFeatureToggleViewBuilder()
                .id(toggleId)
                .projectId(PROJECT_ID)
                .environmentId(ENVIRONMENT_ID)
                .name("N1")
                .description("D1")
                .status(FeatureToggleStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .value(FeatureToggleValueBuilder.bool(true))
                .build();
        sut.insert(existing);

        var updated = fakeFeatureToggleViewBuilder()
                .id(toggleId)
                .projectId(PROJECT_ID)
                .environmentId(ENVIRONMENT_ID)
                .name("SHOULD_NOT_CHANGE")
                .description("SHOULD_NOT_CHANGE")
                .status(FeatureToggleStatus.ARCHIVED)
                .revision(Revision.from(2))
                .consistent(false)
                .createdAt(LocalDateTime.of(2099, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2099, 1, 2, 10, 0))
                .value(FeatureToggleValueBuilder.bool(false))
                .build();

        // when
        sut.updateStatus(updated);

        // then
        var actual = queryRepository.find(toggleId).orElseThrow();

        assertThat(actual.status()).isEqualTo(FeatureToggleStatus.ARCHIVED);
        assertThat(actual.revision()).isEqualTo(Revision.from(2));
        assertThat(actual.consistent()).isFalse();

        assertThat(actual.name()).isEqualTo(existing.name());
        assertThat(actual.description()).isEqualTo(existing.description());
        assertThat(actual.value()).isEqualTo(existing.value());
    }

    @Test
    void should_update_basic_fields_only() {
        // given
        insertProject();
        insertEnvironment();

        var toggleId = FeatureToggleId.create();
        var existing = fakeFeatureToggleViewBuilder()
                .id(toggleId)
                .projectId(PROJECT_ID)
                .environmentId(ENVIRONMENT_ID)
                .name("N1")
                .description("D1")
                .status(FeatureToggleStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .value(FeatureToggleValueBuilder.bool(true))
                .build();
        sut.insert(existing);

        var updated = fakeFeatureToggleViewBuilder()
                .id(toggleId)
                .projectId(PROJECT_ID)
                .environmentId(ENVIRONMENT_ID)
                .name("N2")
                .description("D2")
                .status(FeatureToggleStatus.ARCHIVED)
                .revision(Revision.from(2))
                .consistent(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .value(FeatureToggleValueBuilder.bool(false))
                .build();

        // when
        sut.updateBasicFields(updated);

        // then
        var actual = queryRepository.find(toggleId).orElseThrow();

        assertThat(actual.name()).isEqualTo(updated.name());
        assertThat(actual.description()).isEqualTo(updated.description());
        assertThat(actual.revision()).isEqualTo(Revision.from(2));
        assertThat(actual.consistent()).isFalse();

        assertThat(actual.status()).isEqualTo(existing.status());
        assertThat(actual.value()).isEqualTo(existing.value());
    }

    @Test
    void should_update_value_only() {
        // given
        insertProject();
        insertEnvironment();

        var toggleId = FeatureToggleId.create();
        var existing = fakeFeatureToggleViewBuilder()
                .id(toggleId)
                .projectId(PROJECT_ID)
                .environmentId(ENVIRONMENT_ID)
                .name("N1")
                .description("D1")
                .status(FeatureToggleStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .value(FeatureToggleValueBuilder.bool(true))
                .build();
        sut.insert(existing);

        var updated = fakeFeatureToggleViewBuilder()
                .id(toggleId)
                .projectId(PROJECT_ID)
                .environmentId(ENVIRONMENT_ID)
                .name("SHOULD_NOT_CHANGE")
                .description("SHOULD_NOT_CHANGE")
                .status(FeatureToggleStatus.ARCHIVED)
                .revision(Revision.from(2))
                .consistent(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .value(FeatureToggleValueBuilder.bool(false))
                .build();

        // when
        sut.updateValue(updated);

        // then
        var actual = queryRepository.find(toggleId).orElseThrow();

        assertThat(actual.value()).isEqualTo(updated.value());
        assertThat(actual.revision()).isEqualTo(Revision.from(2));
        assertThat(actual.consistent()).isFalse();

        assertThat(actual.name()).isEqualTo(existing.name());
        assertThat(actual.description()).isEqualTo(existing.description());
        assertThat(actual.status()).isEqualTo(existing.status());
    }

    @Test
    void should_upsert_insert_when_missing() {
        // given
        insertProject();
        insertEnvironment();

        var toggleId = FeatureToggleId.create();
        var expected = fakeFeatureToggleViewBuilder()
                .id(toggleId)
                .projectId(PROJECT_ID)
                .environmentId(ENVIRONMENT_ID)
                .name("N1")
                .description("D1")
                .status(FeatureToggleStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .value(FeatureToggleValueBuilder.bool(true))
                .build();

        // when
        sut.upsert(expected);

        // then
        var actual = queryRepository.find(toggleId).orElseThrow();
        assertThat(actual.name()).isEqualTo(expected.name());
        assertThat(actual.description()).isEqualTo(expected.description());
        assertThat(actual.status()).isEqualTo(expected.status());
        assertThat(actual.revision()).isEqualTo(expected.revision());
        assertThat(actual.consistent()).isTrue();
        assertThat(actual.value()).isEqualTo(expected.value());
    }

    @Test
    void should_upsert_update_when_exists() {
        // given
        insertProject();
        insertEnvironment();

        var toggleId = FeatureToggleId.create();
        var existing = fakeFeatureToggleViewBuilder()
                .id(toggleId)
                .projectId(PROJECT_ID)
                .environmentId(ENVIRONMENT_ID)
                .name("N1")
                .description("D1")
                .status(FeatureToggleStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .value(FeatureToggleValueBuilder.bool(true))
                .build();
        sut.insert(existing);

        var updated = fakeFeatureToggleViewBuilder()
                .id(toggleId)
                .projectId(PROJECT_ID)
                .environmentId(ENVIRONMENT_ID)
                .name("N2")
                .description("D2")
                .status(FeatureToggleStatus.ARCHIVED)
                .revision(Revision.from(2))
                .consistent(false)
                .createdAt(LocalDateTime.of(2099, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 2, 2, 10, 0))
                .value(FeatureToggleValueBuilder.bool(false))
                .build();

        // when
        sut.upsert(updated);

        // then
        var actual = queryRepository.find(toggleId).orElseThrow();

        assertThat(actual.name()).isEqualTo(updated.name());
        assertThat(actual.description()).isEqualTo(updated.description());
        assertThat(actual.status()).isEqualTo(updated.status());
        assertThat(actual.revision()).isEqualTo(updated.revision());
        assertThat(actual.consistent()).isFalse();
        assertThat(actual.value()).isEqualTo(updated.value());

        assertThat(actual.createdAt()).isEqualTo(existing.createdAt());
        assertThat(actual.updatedAt()).isEqualTo(updated.updatedAt());
    }

    @Test
    void should_mark_inconsistent_only_once() {
        // given
        insertProject();
        insertEnvironment();

        var toggleId = FeatureToggleId.create();
        sut.insert(fakeFeatureToggleViewBuilder()
                .id(toggleId)
                .projectId(PROJECT_ID)
                .environmentId(ENVIRONMENT_ID)
                .name("N1")
                .description("D1")
                .status(FeatureToggleStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .value(FeatureToggleValueBuilder.bool(true))
                .build());

        // when
        var first = sut.markInconsistentIfNotMarked(toggleId);
        var second = sut.markInconsistentIfNotMarked(toggleId);

        // then
        assertThat(first).isTrue();
        assertThat(second).isFalse();

        var actual = queryRepository.find(toggleId).orElseThrow();
        assertThat(actual.consistent()).isFalse();
    }

    @Test
    void should_return_false_when_mark_inconsistent_and_row_missing() {
        // given
        var toggleId = FeatureToggleId.create();

        // when
        var result = sut.markInconsistentIfNotMarked(toggleId);

        // then
        assertThat(result).isFalse();
    }

    private void insertProject() {
        projectProjectionRepository.insert(fakeProjectViewBuilder()
                .id(PROJECT_ID)
                .build());
    }

    private void insertEnvironment() {
        environmentProjectionRepository.insert(fakeEnvironmentViewBuilder()
                .id(ENVIRONMENT_ID)
                .projectId(PROJECT_ID)
                .build());
    }
}
