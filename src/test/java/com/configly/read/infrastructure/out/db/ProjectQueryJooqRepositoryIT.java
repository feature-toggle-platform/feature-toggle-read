package com.configly.read.infrastructure.out.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.configly.model.Revision;
import com.configly.model.project.ProjectId;
import com.configly.model.project.ProjectStatus;
import com.configly.read.AbstractITTest;
import com.configly.read.application.port.out.ProjectProjectionRepository;
import com.configly.read.application.port.out.ProjectQueryRepository;
import com.configly.read.domain.ProjectView;

import static org.assertj.core.api.Assertions.assertThat;
import static com.configly.read.builder.FakeProjectViewBuilder.fakeProjectViewBuilder;

class ProjectQueryJooqRepositoryIT extends AbstractITTest {

    @Autowired
    private ProjectQueryRepository sut;

    @Autowired
    private ProjectProjectionRepository projectionRepository;

    @Test
    void should_return_empty_when_project_missing() {
        // given
        var projectId = ProjectId.create();

        // when
        var result = sut.find(projectId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void should_find_project_view_when_exists() {
        // given
        var projectId = ProjectId.create();
        var expectedProjectView = fakeProjectViewBuilder()
                .id(projectId)
                .name("My Project")
                .status(ProjectStatus.ACTIVE)
                .revision(Revision.from(7))
                .consistent(true)
                .build();
        insertProjectView(expectedProjectView);

        // when
        var actual = sut.find(projectId).orElseThrow();

        // then
        assertThat(actual.id()).isEqualTo(projectId);
        assertThat(actual.name().value()).isEqualTo("My Project");
        assertThat(actual.status()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(actual.revision()).isEqualTo(Revision.from(7));
        assertThat(actual.consistent()).isTrue();
    }

    @Test
    void should_find_consistent_project_only_when_consistent_true() {
        // given
        var consistentId = ProjectId.create();
        var inconsistentId = ProjectId.create();
        var inconsistentView = fakeProjectViewBuilder()
                .consistent(false)
                .id(inconsistentId)
                .build();
        var consistentView = fakeProjectViewBuilder()
                .consistent(true)
                .id(consistentId)
                .build();

        insertProjectView(consistentView);
        insertProjectView(inconsistentView);

        // when
        var consistent = sut.findConsistent(consistentId);
        var inconsistent = sut.findConsistent(inconsistentId);

        // then
        assertThat(consistent).isPresent();
        assertThat(inconsistent).isEmpty();
    }

    @Test
    void should_find_projects(){
        // given
        var projectId = ProjectId.create();
        var expectedProjectView = fakeProjectViewBuilder()
                .id(projectId)
                .name("My Project")
                .status(ProjectStatus.ACTIVE)
                .revision(Revision.from(7))
                .consistent(true)
                .build();
        insertProjectView(expectedProjectView);

        // when
        var actual = sut.findAll();

        // then
        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst()).isEqualTo(expectedProjectView);
    }

    private void insertProjectView(ProjectView projectView) {
        projectionRepository.insert(projectView);
    }


}
