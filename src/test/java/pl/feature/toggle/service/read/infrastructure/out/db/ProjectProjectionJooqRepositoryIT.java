package pl.feature.toggle.service.read.infrastructure.out.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.read.AbstractITTest;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.ProjectQueryRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.read.builder.FakeProjectViewBuilder.fakeProjectViewBuilder;

class ProjectProjectionJooqRepositoryIT extends AbstractITTest {

    @Autowired
    private ProjectProjectionRepository sut;

    @Autowired
    private ProjectQueryRepository queryRepository;

    @Test
    void should_insert_project_view() {
        // given
        var projectId = ProjectId.create();
        var expected = fakeProjectViewBuilder()
                .id(projectId)
                .name("P1")
                .description("D1")
                .status(ProjectStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();

        // when
        sut.insert(expected);

        // then
        var actual = queryRepository.find(projectId).orElseThrow();
        assertThat(actual.name()).isEqualTo(expected.name());
        assertThat(actual.description()).isEqualTo(expected.description());
        assertThat(actual.status()).isEqualTo(expected.status());
        assertThat(actual.revision()).isEqualTo(expected.revision());
        assertThat(actual.consistent()).isTrue();
    }

    @Test
    void should_update_status_only() {
        // given
        var projectId = ProjectId.create();
        var existing = fakeProjectViewBuilder()
                .id(projectId)
                .name("P1")
                .description("D1")
                .status(ProjectStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
        sut.insert(existing);

        var updated = fakeProjectViewBuilder()
                .id(projectId)
                .name("SHOULD_NOT_CHANGE")
                .description("SHOULD_NOT_CHANGE")
                .status(ProjectStatus.ARCHIVED)
                .revision(Revision.from(2))
                .consistent(true)
                .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.now())
                .build();

        // when
        sut.updateStatus(updated);


        // then
        var actual = queryRepository.find(projectId).orElseThrow();
        assertThat(actual.status()).isEqualTo(ProjectStatus.ARCHIVED);
        assertThat(actual.revision().value()).isEqualTo(2);
        assertThat(actual.consistent()).isTrue();

        assertThat(actual.name()).isEqualTo(existing.name());
        assertThat(actual.description()).isEqualTo(existing.description());
        assertThat(actual.createdAt()).isEqualTo(CreatedAt.of(LocalDateTime.of(2020, 1, 1, 10, 0)));
    }

    @Test
    void should_update_basic_fields_only() {
        var projectId = ProjectId.create();
        var existing = fakeProjectViewBuilder()
                .id(projectId)
                .name("P1")
                .description("D1")
                .status(ProjectStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
        sut.insert(existing);

        var updated = fakeProjectViewBuilder()
                .id(projectId)
                .name("P2")
                .description("D2")
                .status(ProjectStatus.ARCHIVED)
                .revision(Revision.from(2))
                .consistent(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // when
        sut.updateBasicFields(updated);

        // then
        var actual = queryRepository.find(projectId).orElseThrow();

        assertThat(actual.name()).isEqualTo(updated.name());
        assertThat(actual.description()).isEqualTo(updated.description());
        assertThat(actual.revision()).isEqualTo(updated.revision());
        assertThat(actual.consistent()).isFalse();

        assertThat(actual.status()).isEqualTo(existing.status());
        assertThat(actual.createdAt()).isEqualTo(existing.createdAt());
    }

    @Test
    void should_mark_inconsistent_only_once() {
        // given
        var projectId = ProjectId.create();
        var existing = fakeProjectViewBuilder()
                .id(projectId)
                .name("P1")
                .description("D1")
                .status(ProjectStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
        sut.insert(existing);

        // when
        var first = sut.markInconsistentIfNotMarked(projectId);
        var second = sut.markInconsistentIfNotMarked(projectId);

        // then
        assertThat(first).isTrue();
        assertThat(second).isFalse();
        var actual = queryRepository.find(projectId).orElseThrow();

        assertThat(actual.consistent()).isFalse();
    }

    @Test
    void should_return_false_when_mark_inconsistent_and_row_missing() {
        // given
        var projectId = ProjectId.create();

        // when
        var result = sut.markInconsistentIfNotMarked(projectId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void should_upsert_insert_when_missing() {
        // given
        var projectId = ProjectId.create();
        var expected = fakeProjectViewBuilder()
                .id(projectId)
                .name("P1")
                .description("D1")
                .status(ProjectStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .build();

        // when
        sut.upsert(expected);

        // then
        var actual = queryRepository.find(projectId).orElseThrow();
        assertThat(actual.name()).isEqualTo(expected.name());
        assertThat(actual.description()).isEqualTo(expected.description());
        assertThat(actual.status()).isEqualTo(expected.status());
        assertThat(actual.revision()).isEqualTo(expected.revision());
        assertThat(actual.consistent()).isTrue();

        assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
        assertThat(actual.updatedAt()).isEqualTo(expected.updatedAt());
    }

    @Test
    void should_upsert_update_when_exists() {
        // given
        var projectId = ProjectId.create();
        var existing = fakeProjectViewBuilder()
                .id(projectId)
                .name("P1")
                .description("D1")
                .status(ProjectStatus.ACTIVE)
                .revision(Revision.from(1))
                .consistent(true)
                .createdAt(LocalDateTime.of(2020, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 1, 2, 10, 0))
                .build();
        sut.insert(existing);

        var updated = fakeProjectViewBuilder()
                .id(projectId)
                .name("P2")
                .description("D2")
                .status(ProjectStatus.ARCHIVED)
                .revision(Revision.from(2))
                .consistent(false)
                .createdAt(LocalDateTime.of(2099, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2020, 2, 2, 10, 0))
                .build();

        // when
        sut.upsert(updated);

        // then
        var actual = queryRepository.find(projectId).orElseThrow();

        assertThat(actual.name()).isEqualTo(updated.name());
        assertThat(actual.description()).isEqualTo(updated.description());
        assertThat(actual.status()).isEqualTo(updated.status());
        assertThat(actual.revision()).isEqualTo(updated.revision());
        assertThat(actual.consistent()).isFalse();
        assertThat(actual.createdAt()).isEqualTo(existing.createdAt());
        assertThat(actual.updatedAt()).isEqualTo(updated.updatedAt());
    }

}
