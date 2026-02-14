package pl.feature.toggle.service.read.infrastructure.out.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.read.AbstractITTest;
import pl.feature.toggle.service.read.application.port.out.EnvironmentProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.EnvironmentQueryRepository;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.read.builder.FakeEnvironmentViewBuilder.fakeEnvironmentViewBuilder;
import static pl.feature.toggle.service.read.builder.FakeProjectViewBuilder.fakeProjectViewBuilder;

class EnvironmentProjectionJooqRepositoryIT extends AbstractITTest {

    @Autowired
    private EnvironmentProjectionRepository sut;

    @Autowired
    private EnvironmentQueryRepository environmentQueryRepository;

    @Autowired
    private ProjectProjectionRepository projectProjectionRepository;

    @Test
    void should_insert_environment_view() {
        // given
        insertProject();
        var envId = EnvironmentId.create();
        var expected = fakeEnvironmentViewBuilder()
                .id(envId)
                .projectId(PROJECT_ID)
                .name("ENV-" + UUID.randomUUID())
                .type("TEST")
                .status(EnvironmentStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();

        // when
        sut.insert(expected);

        // then
        var actual = environmentQueryRepository.find(PROJECT_ID, envId).orElseThrow();
        assertThat(actual.id()).isEqualTo(envId);
        assertThat(actual.projectId()).isEqualTo(PROJECT_ID);
        assertThat(actual.name()).isEqualTo(expected.name());
        assertThat(actual.type()).isEqualTo(expected.type());
        assertThat(actual.status()).isEqualTo(expected.status());
        assertThat(actual.revision()).isEqualTo(expected.revision());
        assertThat(actual.consistent()).isTrue();
    }

    @Test
    void should_update_status_only() {
        // given
        insertProject();

        var envId = EnvironmentId.create();
        var existing = fakeEnvironmentViewBuilder()
                .id(envId)
                .projectId(PROJECT_ID)
                .name("ENV1")
                .type("TYPE1")
                .status(EnvironmentStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .build();
        sut.insert(existing);

        var updated = fakeEnvironmentViewBuilder()
                .id(envId)
                .projectId(PROJECT_ID)
                .name("SHOULD_NOT_CHANGE")
                .type("SHOULD_NOT_CHANGE")
                .status(EnvironmentStatus.ARCHIVED)
                .revision(Revision.from(2))
                .consistent(true)
                .createdAt(LocalDateTime.of(2099, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2099, 1, 2, 10, 0))
                .build();

        // when
        sut.updateStatus(updated);

        // then
        var actual = environmentQueryRepository.find(PROJECT_ID, envId).orElseThrow();

        assertThat(actual.status()).isEqualTo(EnvironmentStatus.ARCHIVED);
        assertThat(actual.revision()).isEqualTo(Revision.from(2));
        assertThat(actual.consistent()).isTrue();

        assertThat(actual.name()).isEqualTo(existing.name());
        assertThat(actual.type()).isEqualTo(existing.type());
        assertThat(actual.createdAt()).isEqualTo(existing.createdAt());
    }

    @Test
    void should_update_name_only() {
        // given
        insertProject();
        var envId = EnvironmentId.create();
        var existing = fakeEnvironmentViewBuilder()
                .id(envId)
                .projectId(PROJECT_ID)
                .name("ENV1")
                .type("TYPE1")
                .status(EnvironmentStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .build();
        sut.insert(existing);

        var updated = fakeEnvironmentViewBuilder()
                .id(envId)
                .projectId(PROJECT_ID)
                .name("ENV2")
                .type("SHOULD_NOT_CHANGE")
                .status(EnvironmentStatus.ARCHIVED)
                .revision(Revision.from(2))
                .consistent(false)
                .createdAt(LocalDateTime.of(2099, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2099, 1, 2, 10, 0))
                .build();

        // when
        sut.updateName(updated);

        // then
        var actual = environmentQueryRepository.find(PROJECT_ID, envId).orElseThrow();

        assertThat(actual.name()).isEqualTo(updated.name());
        assertThat(actual.revision()).isEqualTo(Revision.from(2));
        assertThat(actual.consistent()).isFalse();

        assertThat(actual.type()).isEqualTo(existing.type());
        assertThat(actual.status()).isEqualTo(existing.status());
        assertThat(actual.createdAt()).isEqualTo(existing.createdAt());
    }

    @Test
    void should_update_type_only() {
        // given
        insertProject();
        var envId = EnvironmentId.create();
        var existing = fakeEnvironmentViewBuilder()
                .id(envId)
                .projectId(PROJECT_ID)
                .name("ENV1")
                .type("TYPE1")
                .status(EnvironmentStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .build();
        sut.insert(existing);

        var updated = fakeEnvironmentViewBuilder()
                .id(envId)
                .projectId(PROJECT_ID)
                .name("SHOULD_NOT_CHANGE")
                .type("TYPE2")
                .status(EnvironmentStatus.ARCHIVED)
                .revision(Revision.from(2))
                .consistent(false)
                .createdAt(LocalDateTime.of(2099, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2099, 1, 2, 10, 0))
                .build();

        // when
        sut.updateType(updated);

        // then
        var actual = environmentQueryRepository.find(PROJECT_ID, envId).orElseThrow();

        assertThat(actual.type()).isEqualTo("TYPE2");
        assertThat(actual.revision()).isEqualTo(Revision.from(2));
        assertThat(actual.consistent()).isFalse();

        assertThat(actual.name()).isEqualTo(existing.name());
        assertThat(actual.status()).isEqualTo(existing.status());
        assertThat(actual.createdAt()).isEqualTo(existing.createdAt());
    }

    @Test
    void should_upsert_insert_when_missing() {
        // given
        insertProject();
        var envId = EnvironmentId.create();
        var expected = fakeEnvironmentViewBuilder()
                .id(envId)
                .projectId(PROJECT_ID)
                .name("ENV-" + UUID.randomUUID())
                .type("T1")
                .status(EnvironmentStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .build();

        // when
        sut.upsert(expected);

        // then
        var actual = environmentQueryRepository.find(PROJECT_ID, envId).orElseThrow();
        assertThat(actual.name()).isEqualTo(expected.name());
        assertThat(actual.type()).isEqualTo(expected.type());
        assertThat(actual.status()).isEqualTo(expected.status());
        assertThat(actual.revision()).isEqualTo(expected.revision());
        assertThat(actual.consistent()).isTrue();
        assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
        assertThat(actual.updatedAt()).isEqualTo(expected.updatedAt());
    }

    @Test
    void should_upsert_update_when_exists() {
        // given
        insertProject();

        var envId = EnvironmentId.create();
        var existing = fakeEnvironmentViewBuilder()
                .id(envId)
                .projectId(PROJECT_ID)
                .name("ENV1")
                .type("T1")
                .status(EnvironmentStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .build();
        sut.insert(existing);

        var updated = fakeEnvironmentViewBuilder()
                .id(envId)
                .projectId(PROJECT_ID)
                .name("ENV2")
                .type("T2")
                .status(EnvironmentStatus.ARCHIVED)
                .revision(Revision.from(2))
                .consistent(false)
                .createdAt(LocalDateTime.of(2099, 1, 1, 10, 0)) // u Ciebie upsert doUpdate ustawia CREATED_AT -> będzie nadpisane
                .updatedAt(LocalDateTime.of(2020, 2, 2, 10, 0))
                .build();

        // when
        sut.upsert(updated);

        // then
        var actual = environmentQueryRepository.find(PROJECT_ID, envId).orElseThrow();

        assertThat(actual.name()).isEqualTo(updated.name());
        assertThat(actual.type()).isEqualTo(updated.type());
        assertThat(actual.status()).isEqualTo(updated.status());
        assertThat(actual.revision()).isEqualTo(updated.revision());
        assertThat(actual.consistent()).isFalse();

        // UWAGA: u Ciebie upsert doUpdate ustawia CREATED_AT, więc to będzie równe updated.createdAt()
        assertThat(actual.createdAt()).isEqualTo(updated.createdAt());
        assertThat(actual.updatedAt()).isEqualTo(updated.updatedAt());
    }

    @Test
    void should_mark_inconsistent_only_once() {
        // given
        insertProject();
        var envId = EnvironmentId.create();
        var existing = fakeEnvironmentViewBuilder()
                .id(envId)
                .projectId(PROJECT_ID)
                .name("ENV1")
                .type("T1")
                .status(EnvironmentStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .build();
        sut.insert(existing);

        // when
        var first = sut.markInconsistentIfNotMarked(envId);
        var second = sut.markInconsistentIfNotMarked(envId);

        // then
        assertThat(first).isTrue();
        assertThat(second).isFalse();

        var actual = environmentQueryRepository.find(PROJECT_ID, envId).orElseThrow();
        assertThat(actual.consistent()).isFalse();
    }

    @Test
    void should_return_false_when_mark_inconsistent_and_row_missing() {
        // given
        var envId = EnvironmentId.create();

        // when
        var result = sut.markInconsistentIfNotMarked(envId);

        // then
        assertThat(result).isFalse();
    }

    private void insertProject() {
        projectProjectionRepository.insert(fakeProjectViewBuilder()
                .id(PROJECT_ID)
                .build());
    }

}
