package pl.feature.toggle.service.read.infrastructure.out.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.read.AbstractITTest;
import pl.feature.toggle.service.read.application.port.out.EnvironmentProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.EnvironmentQueryRepository;
import pl.feature.toggle.service.read.application.port.out.ProjectProjectionRepository;
import pl.feature.toggle.service.read.domain.EnvironmentView;
import pl.feature.toggle.service.read.domain.ProjectView;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.read.builder.FakeEnvironmentViewBuilder.fakeEnvironmentViewBuilder;
import static pl.feature.toggle.service.read.builder.FakeProjectViewBuilder.fakeProjectViewBuilder;

class EnvironmentQueryJooqRepositoryIT extends AbstractITTest {

    @Autowired
    private EnvironmentQueryRepository sut;

    @Autowired
    private EnvironmentProjectionRepository projectionRepository;

    @Autowired
    private ProjectProjectionRepository projectProjectionRepository;

    @Test
    void should_return_empty_when_environment_missing() {
        var projectId = ProjectId.create();
        var envId = EnvironmentId.create();

        var result = sut.find(projectId, envId);

        assertThat(result).isEmpty();
    }

    @Test
    void should_find_environment_when_project_and_id_match() {
        var projectId = ProjectId.create();
        var envId = EnvironmentId.create();
        var projectView = fakeProjectViewBuilder()
                .id(projectId)
                .build();
        insertProjectView(projectView);
        var environmentView = fakeEnvironmentViewBuilder()
                .projectId(projectId)
                .id(envId)
                .consistent(true)
                .revision(Revision.from(4))
                .name("ENV")
                .type("TEST")
                .build();

        insertEnvironmentView(environmentView);

        var actual = sut.find(projectId, envId).orElseThrow();

        assertThat(actual.projectId()).isEqualTo(projectId);
        assertThat(actual.id()).isEqualTo(envId);
        assertThat(actual.name().value()).isEqualTo("ENV");
        assertThat(actual.type()).isEqualTo("TEST");
        assertThat(actual.status()).isEqualTo(EnvironmentStatus.ACTIVE);
        assertThat(actual.revision()).isEqualTo(Revision.from(4));
        assertThat(actual.consistent()).isTrue();
    }

    @Test
    void should_not_find_environment_when_project_mismatch_even_if_id_matches() {
        var projectA = ProjectId.create();
        var projectB = ProjectId.create();
        var envId = EnvironmentId.create();
        var projectViewA = fakeProjectViewBuilder()
                .id(projectA)
                .build();
        insertProjectView(projectViewA);
        var projectViewB = fakeProjectViewBuilder()
                .id(projectB)
                .build();
        insertProjectView(projectViewB);
        var environmentView = fakeEnvironmentViewBuilder()
                .projectId(projectA)
                .id(envId)
                .consistent(true)
                .revision(Revision.from(4))
                .name("ENV")
                .type("TEST")
                .build();

        insertEnvironmentView(environmentView);

        var result = sut.find(projectB, envId);

        assertThat(result).isEmpty();
    }

    @Test
    void should_find_consistent_only_when_consistent_true_and_project_matches() {
        var projectId = ProjectId.create();
        var consistentEnv = EnvironmentId.create();
        var inconsistentEnv = EnvironmentId.create();
        var projectView = fakeProjectViewBuilder()
                .id(projectId)
                .build();
        insertProjectView(projectView);
        var consistentView = fakeEnvironmentViewBuilder()
                .projectId(projectId)
                .id(consistentEnv)
                .consistent(true)
                .revision(Revision.from(1))
                .build();

        var inconsistentView = fakeEnvironmentViewBuilder()
                .projectId(projectId)
                .id(inconsistentEnv)
                .consistent(false)
                .revision(Revision.from(1))
                .build();


        insertEnvironmentView(consistentView);
        insertEnvironmentView(inconsistentView);

        assertThat(sut.findConsistent(projectId, consistentEnv)).isPresent();
        assertThat(sut.findConsistent(projectId, inconsistentEnv)).isEmpty();
    }

    private void insertEnvironmentView(EnvironmentView environmentView) {
        projectionRepository.insert(environmentView);
    }

    private void insertProjectView(ProjectView projectView) {
        projectProjectionRepository.insert(projectView);
    }

}
